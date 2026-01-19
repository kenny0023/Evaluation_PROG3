import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DishDataRetriever {

    private final DBConnection db = new DBConnection();

    /**
     * Récupère un plat complet avec ses ingrédients et quantités via DishIngredient
     */
    public Dish findDishById(int id) throws SQLException {
        String sql = """
            SELECT
                d.id            AS d_id,
                d.name          AS d_name,
                d.dish_type     AS d_type,
                d.selling_price AS d_selling_price,
                
                i.id            AS i_id,
                i.name          AS i_name,
                i.price         AS i_price,
                i.category      AS i_category,
                
                di.required_quantity,
                di.unit
            FROM Dish d
            LEFT JOIN DishIngredient di ON d.id = di.id_dish
            LEFT JOIN Ingredient i      ON di.id_ingredient = i.id
            WHERE d.id = ?
            ORDER BY i.name
            """;

        try (Connection conn = db.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                Dish dish = null;
                List<Ingredient> ingredients = new ArrayList<>();

                while (rs.next()) {
                    if (dish == null) {
                        dish = new Dish(
                                rs.getInt("d_id"),
                                rs.getString("d_name"),
                                DishTypeEnum.valueOf(rs.getString("d_type")),
                                rs.getDouble("d_selling_price")   // ← plus sûr pour les colonnes NUMERIC
                        );
                    }

                    // Ingrédient présent ?
                    Integer ingredientId = rs.getObject("i_id", Integer.class);
                    if (ingredientId != null) {
                        Ingredient ing = new Ingredient(
                                ingredientId,
                                rs.getString("i_name"),
                                rs.getDouble("i_price"),
                                CategoryEnum.valueOf(rs.getString("i_category")),
                                rs.getDouble("required_quantity"),
                                rs.getString("unit")
                        );
                        ingredients.add(ing);
                    }
                }

                if (dish != null) {
                    for (Ingredient ing : ingredients) {
                        dish.addIngredient(ing);
                    }
                }

                return dish;
            }
        }
    }


    /**
     * Crée ou met à jour un plat avec ses ingrédients (version simplifiée)
     * Note : suppose que les ingrédients existent déjà dans la table Ingredient
     */
    public Dish saveDish(Dish dish) throws SQLException {
        if (dish == null) throw new IllegalArgumentException("Dish ne peut pas être null");

        try (Connection conn = db.getConnection()) {
            conn.setAutoCommit(false);

            Integer dishId = dish.getId();

            // 1. Création ou mise à jour du plat
            if (dishId == null) {
                // Nouveau plat
                String insertDish = """
                    INSERT INTO Dish (name, dish_type, selling_price)
                    VALUES (?, ?::dish_type, ?)
                    RETURNING id
                    """;

                try (PreparedStatement ps = conn.prepareStatement(insertDish)) {
                    ps.setString(1, dish.getName());
                    ps.setString(2, dish.getDishType().name());
                    ps.setObject(3, dish.getSellingPrice(), Types.DOUBLE);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            dishId = rs.getInt(1);
                            dish.setId(dishId);
                        }
                    }
                }
            } else {
                // Mise à jour existant
                String updateDish = """
                    UPDATE Dish
                    SET name = ?,
                        dish_type = ?::dish_type,
                        selling_price = ?
                    WHERE id = ?
                    """;

                try (PreparedStatement ps = conn.prepareStatement(updateDish)) {
                    ps.setString(1, dish.getName());
                    ps.setString(2, dish.getDishType().name());
                    ps.setObject(3, dish.getSellingPrice(), Types.DOUBLE);
                    ps.setInt(4, dishId);
                    ps.executeUpdate();
                }
            }

            // 2. Supprimer les anciennes relations (si mise à jour)
            if (dishId != null) {
                String deleteOld = "DELETE FROM DishIngredient WHERE id_dish = ?";
                try (PreparedStatement ps = conn.prepareStatement(deleteOld)) {
                    ps.setInt(1, dishId);
                    ps.executeUpdate();
                }
            }

            // 3. Insérer les nouvelles relations
            if (!dish.getIngredients().isEmpty()) {
                String insertRelation = """
                    INSERT INTO DishIngredient 
                        (id_dish, id_ingredient, required_quantity, unit)
                    VALUES (?, ?, ?, ?::unit_enum)
                    ON CONFLICT (id_dish, id_ingredient) DO NOTHING
                    """;

                try (PreparedStatement ps = conn.prepareStatement(insertRelation)) {
                    for (Ingredient ing : dish.getIngredients()) {
                        if (ing.getId() == null) {
                            throw new SQLException("L'ingrédient doit avoir un id existant : " + ing.getName());
                        }

                        ps.setInt(1, dishId);
                        ps.setInt(2, ing.getId());
                        ps.setDouble(3, ing.getRequiredQuantity() != null ? ing.getRequiredQuantity() : 1.0);
                        ps.setString(4, ing.getUnit() != null ? ing.getUnit() : "KG");
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }

            conn.commit();

            // Recharger le plat complet pour avoir l'état final
            return findDishById(dishId);
        } catch (SQLException e) {
            // rollback en cas d'erreur
            try (Connection conn = db.getConnection()) {
                if (!conn.getAutoCommit()) {
                    conn.rollback();
                }
            } catch (SQLException ignored) {}
            throw e;
        }
    }
}
