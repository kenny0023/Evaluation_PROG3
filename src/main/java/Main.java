import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        DishDataRetriever retriever = new DishDataRetriever();

        try {
            // Exemple : récupérer et afficher le plat n°1
            System.out.println("=== Affichage du plat n°1 ===");
            Dish plat1 = retriever.findDishById(1);
            if (plat1 != null) {
                System.out.println(plat1);
            } else {
                System.out.println("Plat n°1 non trouvé");
            }

            System.out.println("\n=== Affichage du plat n°2 ===");
            Dish plat2 = retriever.findDishById(2);
            if (plat2 != null) {
                System.out.println(plat2);
            } else {
                System.out.println("Plat n°2 non trouvé");
            }

            // Exemple de création/modification d'un plat (si tu as implémenté saveDish)
            /*
            Dish nouveauPlat = new Dish(null, "Nouvelle soupe", DishTypeEnum.START, 7500.0);

            Ingredient ing1 = new Ingredient(null, "Carotte", 1200.0, CategoryEnum.VEGETABLE);
            ing1.setRequiredQuantity(0.4);
            ing1.setUnit("KG");

            nouveauPlat.addIngredient(ing1);

            Dish saved = retriever.saveDish(nouveauPlat);
            System.out.println("\nNouveau plat enregistré :\n" + saved);
            */

        } catch (SQLException e) {
            System.err.println("Erreur SQL : " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Erreur inattendue : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
