public class Main {
    public static void main(String[] args) {
        DishDataRetriever ddr = new DishDataRetriever();

        try {
            System.out.println("=== a) Plat ID 1 ===");
            System.out.println(ddr.findDishById(1));

            System.out.println("\n=== b) Pagination (page 1, size 3) ===");
            ddr.findIngredients(1, 3).forEach(System.out::println);

            System.out.println("\n=== c & g) Ingrédients contenant 'late' ===");
            ddr.findIngredientsLike("late").forEach(System.out::println);

            System.out.println("\n=== d) Prix moyen par plat ===");
            ddr.findDishWithAverageIngredientPrice();

            System.out.println("\n=== f) Ingrédients VEGETABLE ===");
            ddr.findIngredientsByCategory(CategoryEnum.VEGETABLE).forEach(System.out::println);

            System.out.println("\n=== h) Nom commence par 'Cho' ===");
            ddr.findIngredientsByNameStartingWith("Cho").forEach(System.out::println);

            System.out.println("\n=== i) Création ingrédient Fromage ===");
            Dish dummyDish = new Dish(4, "Gâteau au chocolat", DishTypeEnum.DESSERT);
            Ingredient fromage = new Ingredient(0, "Fromage", 3500.00, CategoryEnum.DAIRY, dummyDish);
            ddr.createIngredient(fromage);

            System.out.println("\n=== j) Mise à jour prix Chocolat ===");
            ddr.updateIngredientPrice(4, 2000.00);

            System.out.println("\n=== k) Sauvegarde nouveau plat ===");
            ddr.saveDish("Pizza Margherita", DishTypeEnum.MAIN);

        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
