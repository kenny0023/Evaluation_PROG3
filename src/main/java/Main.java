public class Main {
    public static void main(String[] args) {
        DishDataRetriever retriever = new DishDataRetriever();

        try {
            int idSoupe = 6;
            Dish soupe = retriever.findDishById(idSoupe);

            if (soupe != null) {
                System.out.println("Plat récupéré : " + soupe.getName());
                System.out.println("Ingrédients :");
                for (Ingredient ing : soupe.getIngredients()) {
                    System.out.println(" - " + ing.getName() + " (prix unitaire: " + ing.getPrice() +
                            " €, quantité: " + (ing.getQuantity() != null ? ing.getQuantity() : "NON FIXÉE") + ")");
                }

                System.out.println("\nTentative de calcul du prix total...");
                try {
                    double prixTotal = soupe.getDishPrice();
                    System.out.println("Prix total : " + prixTotal + " €");
                } catch (IllegalStateException e) {
                    System.out.println("EXCEPTION ATTENDUE (correct) : " + e.getMessage());
                    System.out.println("→ Le prix ne peut pas être calculé car au moins une quantité n'est pas fixée.");
                }
            } else {
                System.out.println("Soupe non trouvée (vérifie l'ID).");
            }

            System.out.println("\nFixation des quantités + calcul réussi :");
            soupe.getIngredients().forEach(ing -> {
                if (ing.getName().equals("Carotte")) ing.setQuantity(0.5);
                if (ing.getName().equals("Poireau")) ing.setQuantity(0.3);
            });
            double prixSoupeOK = soupe.getDishPrice();
            System.out.println("Prix total soupe après fixation : " + prixSoupeOK + " €");

            System.out.println("\nAutres fonctionnalités rapides :");
            System.out.println("Plat ID 1 (Salade fraîche) : " + retriever.findDishById(1));
            System.out.println("Pagination page 1 taille 3 :");
            retriever.findIngredients(1, 3).forEach(System.out::println);
            System.out.println("Ingrédients contenant 'cho' :");
            retriever.findIngredientsLike("cho").forEach(System.out::println);
            System.out.println("Ingrédients VEGETABLE :");
            retriever.findIngredientsByCategory(CategoryEnum.VEGETABLE).forEach(System.out::println);


        } catch (Exception e) {
            System.err.println("Erreur lors des tests : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
