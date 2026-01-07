public class Main {

    public static void main(String[] args) {
        DataRetriever retriever = new DataRetriever();

        System.out.println("1. Récupération du plat ID 4 (Gâteau au chocolat)");
        Dish gateau = retriever.findDishById(4);
        if (gateau != null) {
            System.out.println("   → Plat : " + gateau.getName());
            System.out.println("   → Type : " + gateau.getDishType());
            System.out.println("   → Prix total calculé dynamiquement : " + gateau.getPrice() + " FCFA");
            System.out.println("   → Prix via getDishCost() : " + gateau.getDishCost() + " FCFA");
        } else {
            System.out.println("   ⚠ Plat non trouvé !");
        }
        System.out.println();

        System.out.println("2. Liste paginée des ingrédients (page 1, taille 3)");
        retriever.findAllIngredients(1, 3)
                .forEach(ing -> System.out.println("   • " + ing.getName() +
                        " (" + ing.getPrice() + " FCFA)" +
                        (ing.getDish() != null ? " → " + ing.getDish().getName() : "")));
        System.out.println();

        System.out.println("3. Ingrédients de catégorie VEGETABLE");
        retriever.findIngredientsByCategory(CategoryEnum.VEGETABLE)
                .forEach(ing -> System.out.println("   • " + ing.getName() + " - " + ing.getPrice() + " FCFA"));
        System.out.println();

        System.out.println("4. Recherche d'ingrédients contenant 'cho'");
        retriever.findIngredientsByName("cho")
                .forEach(ing -> System.out.println("   • " + ing.getName() + " - " + ing.getPrice() + " FCFA"));
        System.out.println();

        System.out.println("5. Test complet CRUD : ajout, modification, (suppression commentée)");

        Ingredient sucre = new Ingredient();
        sucre.setName("Sucre cristallisé");
        sucre.setPrice(1800.00);
        sucre.setCategory(CategoryEnum.OTHER);
        sucre.setDish(gateau);

        boolean created = retriever.createIngredient(sucre);
        System.out.println("   → Création réussie : " + created);
        if (created) {
            System.out.println("   → Nouvel ID généré : " + sucre.getId());
            System.out.println("   → Nouveau prix du gâteau : " + retriever.recalculateDishPrice(4) + " FCFA");
        }
        System.out.println();

        if (created) {
            sucre.setPrice(2200.00);
            sucre.setName("Sucre fin");
            boolean updated = retriever.updateIngredient(sucre);
            System.out.println("   → Mise à jour réussie : " + updated);
            System.out.println("   → Prix du gâteau après modification : " + retriever.recalculateDishPrice(4) + " FCFA");
        }
        System.out.println();

        if (created) {
            boolean deleted = retriever.deleteIngredient(sucre.getId());
            System.out.println("   → Suppression réussie : " + deleted);
            System.out.println("   → Prix du gâteau revenu à l'original : " + retriever.recalculateDishPrice(4) + " FCFA");
        }
    }
}