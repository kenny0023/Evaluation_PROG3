import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {

    private final DBConnection dbConnection;
    private static final int DEFAULT_PAGE_SIZE = 10;

    public DataRetriever() {
        this.dbConnection = new DBConnection();
    }

    public Dish findDishById(Integer id) {
        Dish dish = null;

        String sqlDish = "SELECT id, name, dish_type FROM Dish WHERE id = ?";
        String sqlIngredients = "SELECT id, name, price, category FROM Ingredient WHERE id_dish = ?";

        try (Connection conn = dbConnection.getConnection()) {

            try (PreparedStatement psDish = conn.prepareStatement(sqlDish)) {
                psDish.setInt(1, id);
                try (ResultSet rsDish = psDish.executeQuery()) {
                    if (rsDish.next()) {
                        dish = new Dish();
                        dish.setId(rsDish.getInt("id"));
                        dish.setName(rsDish.getString("name"));
                        dish.setDishType(rsDish.getString("dish_type"));
                    }
                }
            }

            if (dish != null) {
                double totalPrice = 0.0;

                try (PreparedStatement psIng = conn.prepareStatement(sqlIngredients)) {
                    psIng.setInt(1, id);
                    try (ResultSet rsIng = psIng.executeQuery()) {
                        while (rsIng.next()) {
                            Ingredient ingredient = new Ingredient();
                            ingredient.setId(rsIng.getInt("id"));
                            ingredient.setName(rsIng.getString("name"));
                            ingredient.setPrice(rsIng.getDouble("price"));
                            ingredient.setCategory(CategoryEnum.valueOf(rsIng.getString("category").toUpperCase()));
                            ingredient.setDish(dish); // Association bidirectionnelle
                            totalPrice += ingredient.getPrice();
                        }
                    }
                }
                dish.setPrice(totalPrice);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du plat ID " + id);
            e.printStackTrace();
        }

        return dish;
    }

    public double recalculateDishPrice(int dishId) {
        Dish dish = findDishById(dishId);
        return (dish != null) ? dish.getPrice() : -1.0;
    }

    public List<Ingredient> findAllIngredients(int page, int size) {
        List<Ingredient> ingredients = new ArrayList<>();
        String sql = """
            SELECT i.id, i.name, i.price, i.category, i.id_dish,
                   d.name AS dish_name, d.dish_type
            FROM Ingredient i
            LEFT JOIN Dish d ON i.id_dish = d.id
            ORDER BY i.id
            LIMIT ? OFFSET ?
            """;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, size);
            ps.setInt(2, (page - 1) * size);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ingredients.add(mapResultSetToIngredient(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ingredients;
    }

    public List<Ingredient> findIngredientsByCategory(CategoryEnum category) {
        List<Ingredient> ingredients = new ArrayList<>();
        String sql = """
            SELECT i.*, d.name AS dish_name, d.dish_type
            FROM Ingredient i
            LEFT JOIN Dish d ON i.id_dish = d.id
            WHERE i.category = ?::category
            """;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, category.name());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ingredients.add(mapResultSetToIngredient(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ingredients;
    }

    public List<Ingredient> findIngredientsByName(String criteria) {
        List<Ingredient> ingredients = new ArrayList<>();
        String sql = """
            SELECT i.*, d.name AS dish_name, d.dish_type
            FROM Ingredient i
            LEFT JOIN Dish d ON i.id_dish = d.id
            WHERE i.name ILIKE ?
            ORDER BY i.name
            """;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + criteria + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ingredients.add(mapResultSetToIngredient(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ingredients;
    }

    public boolean createIngredient(Ingredient ingredient) {
        String sql = "INSERT INTO Ingredient (name, price, category, id_dish) VALUES (?, ?, ?::category, ?) RETURNING id";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ingredient.getName());
            ps.setDouble(2, ingredient.getPrice());
            ps.setString(3, ingredient.getCategory().name());
            if (ingredient.getDish() != null) {
                ps.setInt(4, ingredient.getDish().getId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ingredient.setId(rs.getInt("id"));
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateIngredient(Ingredient ingredient) {
        String sql = "UPDATE Ingredient SET name = ?, price = ?, category = ?::category, id_dish = ? WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ingredient.getName());
            ps.setDouble(2, ingredient.getPrice());
            ps.setString(3, ingredient.getCategory().name());
            if (ingredient.getDish() != null) {
                ps.setInt(4, ingredient.getDish().getId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            ps.setInt(5, ingredient.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteIngredient(int id) {
        String sql = "DELETE FROM Ingredient WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Ingredient mapResultSetToIngredient(ResultSet rs) throws SQLException {
        Ingredient ing = new Ingredient();
        ing.setId(rs.getInt("id"));
        ing.setName(rs.getString("name"));
        ing.setPrice(rs.getDouble("price"));
        ing.setCategory(CategoryEnum.valueOf(rs.getString("category").toUpperCase()));

        if (rs.getObject("id_dish") != null) {
            Dish dish = new Dish();
            dish.setId(rs.getInt("id_dish"));
            dish.setName(rs.getString("dish_name"));
            dish.setDishType(rs.getString("dish_type"));
            ing.setDish(dish);
        }
        return ing;
    }
}