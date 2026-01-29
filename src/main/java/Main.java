import java.math.BigDecimal;
import java.time.Instant;

public class Main {

    public static void main(String[] args) {

        try (DishDataRetriever dr = new DishDataRetriever()) {

            System.out.println("Test getDishCost():");
            testAndPrintCost(dr, 1, "250.00");
            testAndPrintCost(dr, 2, "4500.00");
            testAndPrintCost(dr, 3, "0.00");
            testAndPrintCost(dr, 4, "1400.00");
            testAndPrintCost(dr, 5, "0.00");

            System.out.println("\nTest getGrossMargin():");
            testAndPrintMargin(dr, 1, "3250.00");
            testAndPrintMargin(dr, 2, "7500.00");
            testAndPrintMargin(dr, 3, "EXCEPTION");
            testAndPrintMargin(dr, 4, "6600.00");
            testAndPrintMargin(dr, 5, "EXCEPTION");

            System.out.println("\n=== Test TD4 - Stock ===");
            Ingredient laitue = dr.findIngredientById(1);
            if (laitue != null) {
                Instant t = Instant.parse("2024-06-01T12:08:00Z");
                StockValue stock = laitue.getStockValueAt(t);
                System.out.printf("Stock de %s à %s : %s%n", laitue.getName(), t, stock);
            } else {
                System.out.println("Ingrédient 1 (Laitue) non trouvé");
            }

        } catch (Exception e) {
            System.err.println("❌ Erreur pendant l'exécution :");
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void testAndPrintCost(DishDataRetriever dr, int dishId, String expectedStr) throws Exception {
        BigDecimal expected = new BigDecimal(expectedStr);
        BigDecimal cost = dr.getDishCost(dishId);

        Dish dish = dr.findDishById(dishId);
        String name = (dish != null) ? dish.getName() : "Plat inconnu";

        if (cost.compareTo(expected) == 0) {
            System.out.printf("   ✅ %s: %s%n", name, cost);
        } else {
            System.out.printf("   ❌ %s: %s (attendu: %s)%n", name, cost, expected);
            throw new RuntimeException("Test coût échoué");
        }
    }

    private static void testAndPrintMargin(DishDataRetriever dr, int dishId, String expected) throws Exception {
        Dish dish = dr.findDishById(dishId);
        String name = (dish != null) ? dish.getName() : "Plat inconnu";

        if ("EXCEPTION".equals(expected)) {
            try {
                dr.getGrossMargin(dishId);
                System.out.printf("   ❌ %s: aurait dû lever exception%n", name);
                throw new RuntimeException("Test échoué");
            } catch (IllegalStateException e) {
                System.out.printf("   ✅ %s: Exception OK (%s)%n", name, e.getMessage());
            }
        } else {
            BigDecimal margin = dr.getGrossMargin(dishId);
            BigDecimal expectedVal = new BigDecimal(expected);
            if (margin.compareTo(expectedVal) == 0) {
                System.out.printf("   ✅ %s: %s%n", name, margin);
            } else {
                System.out.printf("   ❌ %s: %s (attendu: %s)%n", name, margin, expected);
                throw new RuntimeException("Test marge échoué");
            }
        }
    }
}
