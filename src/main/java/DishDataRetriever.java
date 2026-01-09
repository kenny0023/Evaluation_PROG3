import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DishDataRetriever {

    private final DBConnection db = new DBConnection();

    public Dish findDishById(Integer id) throws SQLException {
        String sql = """
            SELECT d.id AS d_id, d.name AS d_name, d.dish_type,
                   i.id AS i_id, i.name AS i_name, i.price AS i_price, i.category AS i_category
            FROM Dish d
            LEFT JOIN Ingredient i ON d.id = i.id_dish
            WHERE d.id = ?
            """;

        try (Connection conn = db.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            Dish dish = null;
            while (rs.next()) {
                if (dish == null) {
                    dish = new Dish(rs.getInt("d_id"), rs.getString("d_name"),
                            DishTypeEnum.valueOf(rs.getString("dish_type")));
                }
                if (rs.getObject("i_id") != null) {
                    Ingredient ing = new Ingredient(
                            rs.getInt("i_id"), rs.getString("i_name"),
                            rs.getDouble("i_price"),
                            CategoryEnum.valueOf(rs.getString("i_category")), dish);
                    dish.addIngredient(ing);
                }
            }
            return dish;
        }
    }

    public List<Ingredient> findIngredients(int page, int size) throws SQLException {
        int offset = (page - 1) * size;
        String sql = """
            SELECT i.id, i.name, i.price, i.category, i.id_dish, d.name AS dish_name, d.dish_type
            FROM Ingredient i
            LEFT JOIN Dish d ON i.id_dish = d.id
            ORDER BY i.id
            LIMIT ? OFFSET ?
            """;

        List<Ingredient> list = new ArrayList<>();
        try (Connection conn = db.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, size);
            pstmt.setInt(2, offset);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Dish dish = rs.getObject("id_dish") != null ?
                        new Dish(rs.getInt("id_dish"), rs.getString("dish_name"),
                                DishTypeEnum.valueOf(rs.getString("dish_type"))) : null;
                Ingredient ing = new Ingredient(rs.getInt("id"), rs.getString("name"),
                        rs.getDouble("price"), CategoryEnum.valueOf(rs.getString("category")), dish);
                list.add(ing);
            }
        }
        return list;
    }

    public List<Ingredient> findIngredientsLike(String keyword) throws SQLException {
        String sql = """
            SELECT i.*, d.name AS dish_name, d.dish_type
            FROM Ingredient i LEFT JOIN Dish d ON i.id_dish = d.id
            WHERE LOWER(i.name) LIKE LOWER(?)
            """;

        List<Ingredient> list = new ArrayList<>();
        try (Connection conn = db.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Dish dish = rs.getObject("id_dish") != null ?
                        new Dish(rs.getInt("id_dish"), rs.getString("dish_name"),
                                DishTypeEnum.valueOf(rs.getString("dish_type"))) : null;
                Ingredient ing = new Ingredient(rs.getInt("id"), rs.getString("name"),
                        rs.getDouble("price"), CategoryEnum.valueOf(rs.getString("category")), dish);
                list.add(ing);
            }
        }
        return list;
    }

    public List<Dish> findDishWithAverageIngredientPrice() throws SQLException {
        String sql = """
            SELECT d.id, d.name, d.dish_type, AVG(i.price) AS avg_price
            FROM Dish d LEFT JOIN Ingredient i ON d.id = i.id_dish
            GROUP BY d.id
            ORDER BY avg_price DESC
            """;

        List<Dish> list = new ArrayList<>();
        try (Connection conn = db.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Dish dish = new Dish(rs.getInt("id"), rs.getString("name"),
                        DishTypeEnum.valueOf(rs.getString("dish_type")));
                list.add(dish);
                System.out.printf("Plat: %s → Prix moyen ingrédients: %.2f €%n",
                        dish.getName(), rs.getDouble("avg_price"));
            }
        }
        return list;
    }

    public List<Ingredient> findIngredientsByCategory(CategoryEnum category) throws SQLException {
        String sql = """
            SELECT i.*, d.name AS dish_name, d.dish_type
            FROM Ingredient i LEFT JOIN Dish d ON i.id_dish = d.id
            WHERE i.category = ?::ingredient_category
            """;

        List<Ingredient> list = new ArrayList<>();
        try (Connection conn = db.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, category.name());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Dish dish = rs.getObject("id_dish") != null ?
                        new Dish(rs.getInt("id_dish"), rs.getString("dish_name"),
                                DishTypeEnum.valueOf(rs.getString("dish_type"))) : null;
                Ingredient ing = new Ingredient(rs.getInt("id"), rs.getString("name"),
                        rs.getDouble("price"), CategoryEnum.valueOf(rs.getString("category")), dish);
                list.add(ing);
            }
        }
        return list;
    }

    public List<Ingredient> findIngredientsByCriteria(String criteria) throws SQLException {
        return findIngredientsLike(criteria);
    }

    public List<Ingredient> findIngredientsByNameStartingWith(String prefix) throws SQLException {
        String sql = """
            SELECT i.*, d.name AS dish_name, d.dish_type
            FROM Ingredient i LEFT JOIN Dish d ON i.id_dish = d.id
            WHERE LOWER(i.name) LIKE LOWER(?)
            """;

        List<Ingredient> list = new ArrayList<>();
        try (Connection conn = db.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, prefix + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Dish dish = rs.getObject("id_dish") != null ?
                        new Dish(rs.getInt("id_dish"), rs.getString("dish_name"),
                                DishTypeEnum.valueOf(rs.getString("dish_type"))) : null;
                Ingredient ing = new Ingredient(rs.getInt("id"), rs.getString("name"),
                        rs.getDouble("price"), CategoryEnum.valueOf(rs.getString("category")), dish);
                list.add(ing);
            }
        }
        return list;
    }

    public void createIngredient(Ingredient ingredient) throws SQLException {
        String sql = "INSERT INTO Ingredient (name, price, category, id_dish) VALUES (?, ?, ?::ingredient_category, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ingredient.getName());
            pstmt.setDouble(2, ingredient.getPrice());
            pstmt.setString(3, ingredient.getCategory().name());
            Integer dishId = ingredient.getDish() != null ? ingredient.getDish().getId() : null;
            if (dishId != null) pstmt.setInt(4, dishId);
            else pstmt.setNull(4, Types.INTEGER);
            pstmt.executeUpdate();
            System.out.println("Ingrédient créé : " + ingredient.getName());
        }
    }

    public void updateIngredientPrice(int ingredientId, double newPrice) throws SQLException {
        String sql = "UPDATE Ingredient SET price = ? WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, newPrice);
            pstmt.setInt(2, ingredientId);
            int rows = pstmt.executeUpdate();
            System.out.println(rows > 0 ? "Prix mis à jour !" : "Ingrédient non trouvé");
        }
    }

    public void saveDish(String name, DishTypeEnum type) throws SQLException {
        String sql = "INSERT INTO Dish (name, dish_type) VALUES (?, ?::dish_type)";
        try (Connection conn = db.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, type.name());
            pstmt.executeUpdate();
            System.out.println("Plat sauvegardé : " + name);
        }
    }

    public void saveDishWithIngredients(Dish dish) throws SQLException {
        String sqlDish = "INSERT INTO Dish (name, dish_type) VALUES (?, ?::dish_type) RETURNING id";
        String sqlIng = "INSERT INTO Ingredient (name, price, category, id_dish) VALUES (?, ?, ?::ingredient_category, ?)";

        try (Connection conn = db.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmtDish = conn.prepareStatement(sqlDish)) {
                pstmtDish.setString(1, dish.getName());
                pstmtDish.setString(2, dish.getDishType().name());
                ResultSet rs = pstmtDish.executeQuery();
                if (rs.next()) {
                    int dishId = rs.getInt(1);

                    try (PreparedStatement pstmtIng = conn.prepareStatement(sqlIng)) {
                        for (Ingredient ing : dish.getIngredients()) {
                            pstmtIng.setString(1, ing.getName());
                            pstmtIng.setDouble(2, ing.getPrice());
                            pstmtIng.setString(3, ing.getCategory().name());
                            pstmtIng.setInt(4, dishId);
                            pstmtIng.addBatch();
                        }
                        pstmtIng.executeBatch();
                    }
                }
                conn.commit();
                System.out.println("Plat et ses ingrédients sauvegardés avec succès !");
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        }
    }
}
