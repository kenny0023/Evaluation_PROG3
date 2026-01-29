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

        BigDecimal price = dish.getSellingPrice();
        if (price == null) {
            throw new IllegalStateException("Prix de vente non défini pour " + dish.getName());
        }

        BigDecimal cost = getDishCost(dishId);
        return price.subtract(cost).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}