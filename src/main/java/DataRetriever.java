import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {
    public static Dish findDishById(Integer id) {
        Dish dish = null;

        String sqlDish = "SELECT id, name, dish_type FROM Dish WHERE id = ?";
        String sqlIngredients = "SELECT id, name, price, category FROM Ingredient WHERE id_dish = ?";

        DBConnection dbConn = new DBConnection();

        try (Connection conn = dbConn.getConnection()) {

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
                            String catStr = rsIng.getString("category");
                            ingredient.setCategory(CategoryEnum.valueOf(catStr.toUpperCase()));
                            ingredient.setDish(dish);

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

    public static void main(String[] args) {
        Dish dish = findDishById(4);

        if (dish != null) {
            System.out.println("Plat trouvé : " + dish);
            System.out.println("Prix total : " + dish.getPrice() + " FCFA");
        } else {
            System.out.println("Plat non trouvé.");
        }
    }
}
