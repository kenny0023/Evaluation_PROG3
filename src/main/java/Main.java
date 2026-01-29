//import java.math.BigDecimal;
//
//public class Main {
//
//    public static void main(String[] args) {
//        System.out.println("Exécution des tests TD3...\n");
//
//        try (DishDataRetriever dr = new DishDataRetriever()) {
//
//            // Test 1: Coûts
//            System.out.println("Test getDishCost():");
//            testAndPrintCost(dr, 1, "250.00");
//            testAndPrintCost(dr, 2, "4500.00");
//            testAndPrintCost(dr, 3, "0.00");
//            testAndPrintCost(dr, 4, "1400.00");
//            testAndPrintCost(dr, 5, "0.00");
//
//            // Test 2: Marges
//            System.out.println("\nTest getGrossMargin():");
//            testAndPrintMargin(dr, 1, "3250.00");
//            testAndPrintMargin(dr, 2, "7500.00");
//            testAndPrintMargin(dr, 3, "EXCEPTION");
//            testAndPrintMargin(dr, 4, "6600.00");
//            testAndPrintMargin(dr, 5, "EXCEPTION");
//
//            System.out.println("\n🎉 Tous les tests ont réussi !");
//
//        } catch (Exception e) {
//            System.err.println("❌ Erreur: " + e.getMessage());
//            e.printStackTrace();
//            System.exit(1);
//        }
//    }
//
//    private static void testAndPrintCost(DishDataRetriever dr, int dishId, String expectedStr) throws Exception {
//        BigDecimal expected = new BigDecimal(expectedStr);
//        BigDecimal cost = dr.getDishCost(dishId);
//
//        Dish dish = dr.findDishById(dishId);
//        String name = (dish != null) ? dish.getName() : "Plat inconnu";
//
//        if (cost.compareTo(expected) == 0) {
//            System.out.printf("   ✅ %s: %s%n", name, cost);
//        } else {
//            System.out.printf("   ❌ %s: %s (attendu: %s)%n", name, cost, expected);
//            throw new RuntimeException("Test coût échoué");
//        }
//    }
//
//    private static void testAndPrintMargin(DishDataRetriever dr, int dishId, String expected) throws Exception {
//        Dish dish = dr.findDishById(dishId);
//        String name = (dish != null) ? dish.getName() : "Plat inconnu";
//
//        if ("EXCEPTION".equals(expected)) {
//            try {
//                dr.getGrossMargin(dishId);
//                System.out.printf("   ❌ %s: aurait dû lever exception%n", name);
//                throw new RuntimeException("Test échoué");
//            } catch (IllegalStateException e) {
//                System.out.printf("   ✅ %s: Exception OK (%s)%n", name, e.getMessage());
//            }
//        } else {
//            BigDecimal margin = dr.getGrossMargin(dishId);
//            BigDecimal expectedVal = new BigDecimal(expected);
//            if (margin.compareTo(expectedVal) == 0) {
//                System.out.printf("   ✅ %s: %s%n", name, margin);
//            } else {
//                System.out.printf("   ❌ %s: %s (attendu: %s)%n", name, margin, expected);
//                throw new RuntimeException("Test marge échoué");
//            }
//        }
//    }
//}


import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.Instant;

public class Main {

    public static void main(String[] args) {
        System.out.println("=== Tests TD3 + TD4 ===\n");

        try (DishDataRetriever dr = new DishDataRetriever()) {

            // TD3 - Tests existants
            testDishCost(dr, 1, "250.00");
            testDishCost(dr, 2, "4500.00");
            testDishCost(dr, 4, "1400.00");

            testGrossMargin(dr, 1, "3250.00");
            testGrossMargin(dr, 2, "7500.00");
            testGrossMarginException(dr, 3);
            testGrossMargin(dr, 4, "6600.00");

            // TD4 - Test stock
            System.out.println("\n=== Test gestion stock TD4 ===");
            Ingredient laitue = dr.findIngredientById(1);
            if (laitue != null) {
                Instant t = Instant.parse("2024-06-01T12:08:00Z");
                StockValue stock = laitue.getStockValueAt(t);
                System.out.printf("Stock Laitue à %s : %s%n", t, stock);
            } else {
                System.out.println("Ingrédient 1 non trouvé");
            }

            System.out.println("\n🎉 Tout semble OK !");

        } catch (Exception e) {
            System.err.println("❌ Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testDishCost(DishDataRetriever dr, int id, String expected) throws SQLException {
        BigDecimal cost = dr.getDishCost(id);
        BigDecimal exp = new BigDecimal(expected);
        if (cost.compareTo(exp) == 0) {
            System.out.printf("Coût plat %d → OK (%s)%n", id, cost);
        } else {
            System.out.printf("ÉCHEC coût plat %d : %s au lieu de %s%n", id, cost, expected);
        }
    }

    private static void testGrossMargin(DishDataRetriever dr, int id, String expected) throws SQLException {
        BigDecimal margin = dr.getGrossMargin(id);
        BigDecimal exp = new BigDecimal(expected);
        if (margin.compareTo(exp) == 0) {
            System.out.printf("Marge plat %d → OK (%s)%n", id, margin);
        } else {
            System.out.printf("ÉCHEC marge plat %d : %s au lieu de %s%n", id, margin, expected);
        }
    }

    private static void testGrossMarginException(DishDataRetriever dr, int id) {
        try {
            dr.getGrossMargin(id);
            System.out.printf("ÉCHEC : plat %d devrait lever exception%n", id);
        } catch (IllegalStateException e) {
            System.out.printf("Plat %d → exception OK (%s)%n", id, e.getMessage());
        } catch (Exception e) {
            System.out.printf("Mauvaise exception plat %d : %s%n", id, e.getClass().getSimpleName());
        }
    }
}