import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;

public class DishDataRetriever implements AutoCloseable {

    private final Connection connection;

    public DishDataRetriever() throws SQLException {
        this.connection = DBConnection.getDBConnection();
    }

    public Dish findDishById(int id) throws SQLException {
        String sql = """
            SELECT id, name, dish_type, selling_price
            FROM dish
            WHERE id = ?
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                Dish dish = new Dish();
                dish.setId(rs.getInt("id"));
                dish.setName(rs.getString("name"));

                String typeStr = rs.getString("dish_type");
                if (typeStr != null) {
                    dish.setDishType(DishTypeEnum.valueOf(typeStr.toUpperCase()));
                }

                BigDecimal price = rs.getBigDecimal("selling_price");
                if (!rs.wasNull()) {
                    dish.setSellingPrice(price);
                }

                return dish;
            }
        }
    }

    public BigDecimal getDishCost(int dishId) throws SQLException {
        String sql = """
            SELECT COALESCE(SUM(i.price * di.quantity_required), 0) AS total_cost
            FROM dish d
            LEFT JOIN dish_ingredient di ON d.id = di.id_dish
            LEFT JOIN ingredient i ON di.id_ingredient = i.id
            WHERE d.id = ?
            GROUP BY d.id
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, dishId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("total_cost")
                            .setScale(2, RoundingMode.HALF_UP);
                }
                return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            }
        }
    }

    public BigDecimal getGrossMargin(int dishId) throws SQLException {
        Dish dish = findDishById(dishId);
        if (dish == null) {
            throw new SQLException("Plat non trouvé id=" + dishId);
        }

        BigDecimal sellingPrice = dish.getSellingPrice();
        if (sellingPrice == null) {
            throw new IllegalStateException("Prix de vente non défini pour " + dish.getName());
        }

        BigDecimal cost = getDishCost(dishId);
        return sellingPrice.subtract(cost).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * CHARGEMENT INGREDIENT + SES MOUVEMENTS DE STOCK (TD4)
     */
    public Ingredient findIngredientById(int id) throws SQLException {
        String sqlIng = """
            SELECT id, name, price, category
            FROM ingredient
            WHERE id = ?
            """;

        Ingredient ing = null;

        try (PreparedStatement ps = connection.prepareStatement(sqlIng)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ing = new Ingredient(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getBigDecimal("price"),
                            CategoryEnum.valueOf(rs.getString("category"))
                    );
                }
            }
        }

        if (ing == null) return null;

        // Charger les mouvements
        String sqlMov = """
            SELECT id, id_ingredient, quantity, unit, type, creation_datetime
            FROM stock_movement
            WHERE id_ingredient = ?
            ORDER BY creation_datetime
            """;

        try (PreparedStatement ps = connection.prepareStatement(sqlMov)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StockMovement m = new StockMovement();
                    m.setId(rs.getInt("id"));
                    m.setIngredientId(rs.getInt("id_ingredient"));
                    m.setQuantity(rs.getBigDecimal("quantity"));
                    m.setUnit(UnitTypeEnum.valueOf(rs.getString("unit")));
                    m.setType(MovementTypeEnum.valueOf(rs.getString("type")));
                    m.setCreationDatetime(rs.getTimestamp("creation_datetime").toInstant());
                    ing.addStockMovement(m);
                }
            }
        }

        return ing;
    }

    /**
     * SAUVEGARDE INGREDIENT + NOUVEAUX MOUVEMENTS (TD4)
     */
    public Ingredient saveIngredient(Ingredient ingredient) throws SQLException {
        if (ingredient == null || ingredient.getName() == null) {
            throw new IllegalArgumentException("Ingrédient invalide");
        }

        Connection conn = DBConnection.getDBConnection();
        boolean autoCommit = conn.getAutoCommit();

        try {
            conn.setAutoCommit(false);

            // 1. Sauvegarde / update ingrédient
            String sqlIng = """
                INSERT INTO ingredient (name, price, category)
                VALUES (?, ?, ?)
                ON CONFLICT (name) DO UPDATE SET
                    price = EXCLUDED.price,
                    category = EXCLUDED.category
                RETURNING id
                """;

            try (PreparedStatement ps = conn.prepareStatement(sqlIng)) {
                ps.setString(1, ingredient.getName());
                ps.setBigDecimal(2, ingredient.getPrice());
                ps.setString(3, ingredient.getCategory().name());
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    ingredient.setId(rs.getInt(1));
                }
            }

            // 2. Sauvegarde nouveaux mouvements
            String sqlMvt = """
                INSERT INTO stock_movement (id_ingredient, quantity, unit, type, creation_datetime)
                VALUES (?, ?, ?::unit_enum, ?::movement_type, ?)
                RETURNING id
                """;

            try (PreparedStatement ps = conn.prepareStatement(sqlMvt)) {
                for (StockMovement m : ingredient.getStockMovements()) {
                    if (m.getId() == null) { // seulement nouveaux
                        ps.setInt(1, ingredient.getId());
                        ps.setBigDecimal(2, m.getQuantity());
                        ps.setString(3, m.getUnit().name());
                        ps.setString(4, m.getType().name());
                        ps.setTimestamp(5, Timestamp.from(m.getCreationDatetime()));
                        ResultSet rs = ps.executeQuery();
                        if (rs.next()) {
                            m.setId(rs.getInt(1));
                        }
                    }
                }
            }

            conn.commit();
            return ingredient;

        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(autoCommit);
                conn.close();
            }
        }
    }

    @Override
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
