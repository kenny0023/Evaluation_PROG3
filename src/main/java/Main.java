//import java.math.BigDecimal;
//
//public class Main {
//
//    public static void main(String[] args) {
//        System.out.println("Exécution des tests TD3...");
//
//        try (DishDataRetriever dr = new DishDataRetriever()) {
//
//            // Test 1: Coûts
//            System.out.println("\nTest getDishCost():");
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
//    private static void testAndPrintCost(DishDataRetriever dr, int dishId, String expected) throws Exception {
//        Dish dish = dr.findDishById(dishId);
//        BigDecimal cost = dr.getDishCost(dishId);
//
//        String name = (dish != null) ? dish.getName() : "Plat inconnu";
//
//        if (cost.toString().equals(expected)) {
//            System.out.printf("    %s: %s%n", name, cost);
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
//                System.out.printf("   ✅ %s: Exception (OK)%n", name);
//            }
//        } else {
//            BigDecimal margin = dr.getGrossMargin(dishId);
//            if (margin.toString().equals(expected)) {
//                System.out.printf("   ✅ %s: %s%n", name, margin);
//            } else {
//                System.out.printf("   ❌ %s: %s (attendu: %s)%n", name, margin, expected);
//                throw new RuntimeException("Test marge échoué");
//            }
//        }
//    }
//}

import java.time.Instant;

public class Main {
    public static void main(String[] args) {
        Ingredient laitue = new Ingredient(1, "Laitue", 800.0, CategoryEnum.VEGETABLE);

        // Mouvements de test
        laitue.addStockMovement(new StockMovement() {{
            setQuantity(5.0);
            setUnit(UnitTypeEnum.KG);
            setType(MovementTypeEnum.IN);
            setCreationDatetime(Instant.parse("2024-01-05T10:00:00Z"));
        }});

        laitue.addStockMovement(new StockMovement() {{
            setQuantity(0.2);
            setUnit(UnitTypeEnum.KG);
            setType(MovementTypeEnum.OUT);
            setCreationDatetime(Instant.parse("2024-01-06T14:00:00Z"));
        }});

        Instant t = Instant.parse("2024-06-01T12:08:00Z");
        StockValue stock = laitue.getStockValueAt(t);

        System.out.println("Stock Laitue à " + t + " : " + stock);
        // Résultat attendu : 4.80 KG
    }
}