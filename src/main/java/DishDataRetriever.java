import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.time.Instant;

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

    public Ingredient saveIngredient(Ingredient ingredient) throws SQLException {
        if (ingredient == null || ingredient.getName() == null || ingredient.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Ingrédient ou nom obligatoire");
        }

        Connection conn = DBConnection.getDBConnection();
        boolean autoCommit = conn.getAutoCommit();

        try {
            conn.setAutoCommit(false);

            String sqlIng = """
                INSERT INTO ingredient (name, price, category)
                VALUES (?, ?, ?::category)
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

            String sqlMvt = """
                INSERT INTO stock_movement (id_ingredient, quantity, unit, type, creation_datetime)
                VALUES (?, ?, ?::unit_enum, ?::movement_type, ?)
                RETURNING id
                """;

            try (PreparedStatement ps = conn.prepareStatement(sqlMvt)) {
                for (StockMovement m : ingredient.getStockMovements()) {
                    if (m.getId() == null) {
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

    public Order saveOrder(Order orderToSave) throws SQLException {
        if (orderToSave == null || orderToSave.getReference() == null || orderToSave.getDishOrders().isEmpty()) {
            throw new IllegalArgumentException("Commande invalide ou vide");
        }

        Connection conn = DBConnection.getDBConnection();
        boolean autoCommit = conn.getAutoCommit();

        try {
            conn.setAutoCommit(false);

            for (DishOrder doItem : orderToSave.getDishOrders()) {
                Dish dish = doItem.getDish();
                if (dish == null) continue;

                for (DishIngredient di : dish.getDishIngredients()) {
                    Ingredient ing = di.getIngredient();
                    if (ing == null) continue;

                    BigDecimal needed = di.getQuantityRequired().multiply(BigDecimal.valueOf(doItem.getQuantity()));

                    StockValue current = ing.getStockValueAt(Instant.now());
                    if (current.getQuantity().compareTo(needed) < 0) {
                        throw new IllegalStateException(
                                String.format("Stock insuffisant pour %s : besoin %.2f %s, disponible %.2f %s",
                                        ing.getName(), needed, di.getUnit(), current.getQuantity(), current.getUnit())
                        );
                    }
                }
            }

            String sqlOrder = """
                INSERT INTO "order" (reference, creation_datetime, total_ttc)
                VALUES (?, ?, ?)
                RETURNING id
                """;

            Integer orderId;
            try (PreparedStatement ps = conn.prepareStatement(sqlOrder)) {
                ps.setString(1, orderToSave.getReference());
                ps.setTimestamp(2, Timestamp.from(orderToSave.getCreationDatetime() != null
                        ? orderToSave.getCreationDatetime() : Instant.now()));
                ps.setBigDecimal(3, orderToSave.getTotalTtc());
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    orderId = rs.getInt("id");
                    orderToSave.setId(orderId);
                } else {
                    throw new SQLException("Échec création commande");
                }
            }

            String sqlDishOrder = """
                INSERT INTO dish_order (id_order, id_dish, quantity)
                VALUES (?, ?, ?)
                RETURNING id
                """;

            try (PreparedStatement ps = conn.prepareStatement(sqlDishOrder)) {
                for (DishOrder doItem : orderToSave.getDishOrders()) {
                    ps.setInt(1, orderId);
                    ps.setInt(2, doItem.getDish().getId());
                    ps.setInt(3, doItem.getQuantity());
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        doItem.setId(rs.getInt("id"));
                    }
                }
            }

            conn.commit();
            return orderToSave;

        } catch (SQLException | IllegalStateException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(autoCommit);
                conn.close();
            }
        }
    }

    public Order findOrderByReference(String reference) throws SQLException {
        if (reference == null || reference.trim().isEmpty()) {
            throw new IllegalArgumentException("Référence obligatoire");
        }

        String sqlOrder = """
        SELECT id, reference, creation_datetime, total_ttc
        FROM "order"
        WHERE reference = ?
        """;

        Order order = null;

        try (PreparedStatement ps = connection.prepareStatement(sqlOrder)) {
            ps.setString(1, reference);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    order = new Order();
                    order.setId(rs.getInt("id"));
                    order.setReference(rs.getString("reference"));
                    order.setCreationDatetime(rs.getTimestamp("creation_datetime").toInstant());
                    order.setTotalTtc(rs.getBigDecimal("total_ttc"));
                }
            }
        }

        if (order == null) {
            throw new IllegalArgumentException("Commande non trouvée pour référence : " + reference);
        }

        String sqlLines = """
        SELECT do.id, do.id_order, do.id_dish, do.quantity, d.name, d.selling_price
        FROM dish_order do
        JOIN dish d ON do.id_dish = d.id
        WHERE do.id_order = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sqlLines)) {
            ps.setInt(1, order.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DishOrder doItem = new DishOrder();
                    doItem.setId(rs.getInt("id"));
                    doItem.setQuantity(rs.getInt("quantity"));

                    Dish dish = new Dish();
                    dish.setId(rs.getInt("id_dish"));
                    dish.setName(rs.getString("name"));
                    dish.setSellingPrice(rs.getBigDecimal("selling_price"));

                    doItem.setDish(dish);
                    order.addDishOrder(doItem);
                }
            }
        }

        return order;
    }

    @Override
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
