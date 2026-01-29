//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.junit.jupiter.api.Assumptions.assumeTrue;
//
//import java.sql.SQLException;
//
//class DishDataRetrieverTest {
//
//    private DishDataRetriever retriever;
//
//    @BeforeEach
//    void setUp() {
//        retriever = new DishDataRetriever();
//    }
//
//    @Test
//    void testFindDishById_ExistingDish_ReturnsDishWithIngredients() throws SQLException {
//        Dish dish = retriever.findDishById(1);
//
//        assertNotNull(dish, "Le plat ne devrait pas être null");
//
//        String normalizedName = dish.getName()
//                .replace("Œ", "Oe")
//                .replace("œ", "oe")
//                .replaceAll("[îï]", "i")
//                .trim()
//                .toLowerCase();
//
//        assertTrue(
//                normalizedName.contains("salade") && normalizedName.contains("fra"),
//                "Le nom du plat devrait contenir 'salade' et 'fraîche' (tolérance accents)"
//        );
//
//        assertEquals(DishTypeEnum.START, dish.getDishType(),
//                "Le type de plat devrait être START");
//
//        boolean hasVegetableLike = dish.getIngredients().stream()
//                .anyMatch(ing -> {
//                    String n = ing.getName().trim().toLowerCase();
//                    return n.contains("laitue") || n.contains("salade") || n.contains("tomate");
//                });
//
//        assertTrue(hasVegetableLike,
//                "Le plat devrait contenir au moins un ingrédient de type salade/laitue/tomate (insensible casse)");
//    }
//
//    @Test
//    void testFindDishById_NonExistingId_ReturnsNull() throws SQLException {
//        Dish dish = retriever.findDishById(9999);
//        assertNull(dish, "Un id inexistant devrait retourner null");
//    }
//
//    @Test
//    void testDishCostCalculation_NotNegative() throws SQLException {
//        Dish dish = retriever.findDishById(1);
//
//        assertNotNull(dish);
//
//        double cost = dish.getCostPrice();
//        assertTrue(cost >= 0, "Le coût matière ne devrait jamais être négatif");
//    }
//
//    @Test
//    void testDishWithSellingPrice_HasValidMargin() throws SQLException {
//
//        Dish dish = retriever.findDishById(2);
//
//        assumeTrue(dish != null, "Ce test suppose que le plat 2 existe");
//        assumeTrue(dish.getSellingPrice() != null, "Ce test suppose un prix de vente");
//
//        double margin = dish.getMargin();
//        double marginPercent = dish.getMarginPercent();
//
//        assertTrue(margin >= 0, "La marge brute ne devrait pas être négative");
//        assertTrue(marginPercent >= 0 && marginPercent <= 100,
//                "Le pourcentage de marge devrait être entre 0% et 100%");
//    }
//}


import org.junit.jupiter.api.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DishDataRetrieverTest {

    private DishDataRetriever retriever;
    private DishDataRetriever dr;

    @BeforeAll
    void setUp() throws SQLException {
        retriever = new DishDataRetriever();
    }

    @AfterAll
    void tearDown() throws SQLException {
        if (retriever != null) {
            retriever.close();
        }
    }

    @Test
    void getDishCost_existingDish_returnsCorrectCost() throws SQLException {
        BigDecimal cost = retriever.getDishCost(1);
        assertEquals(new BigDecimal("250.00"), cost.setScale(2));
    }

    @Test
    void getGrossMargin_existingDishWithPrice_returnsCorrectMargin() throws SQLException {
        BigDecimal margin = retriever.getGrossMargin(1);
        assertEquals(new BigDecimal("3250.00"), margin.setScale(2));
    }

    @Test
    void getGrossMargin_dishWithoutPrice_throwsIllegalStateException() {
        assertThrows(IllegalStateException.class, () -> retriever.getGrossMargin(3));
    }

    @Test
    void findIngredientById_existingId_returnsIngredientWithMovements() throws SQLException {
        Ingredient ing = retriever.findIngredientById(1);
        assertNotNull(ing);
        assertEquals("Laitue", ing.getName());
        assertFalse(ing.getStockMovements().isEmpty());

        Instant t = Instant.parse("2024-06-01T12:08:00Z");
        StockValue stock = ing.getStockValueAt(t);
        assertEquals(new BigDecimal("4.80"), stock.getQuantity().setScale(2));
    }

    @Test
    void saveIngredient_newIngredientWithMovements_savesAndReturnsId() throws SQLException {
        String uniqueName = "TestIngr-" + System.currentTimeMillis();

        Ingredient newIng = new Ingredient();
        newIng.setName(uniqueName);
        newIng.setPrice(new BigDecimal("12000.00"));
        newIng.setCategory(CategoryEnum.OTHER);

        newIng.addStockMovement(new StockMovement() {{
            setQuantity(new BigDecimal("5.0"));
            setUnit(UnitTypeEnum.KG);
            setType(MovementTypeEnum.IN);
            setCreationDatetime(Instant.now().minus(1, ChronoUnit.DAYS));
        }});

        Ingredient saved = retriever.saveIngredient(newIng);

        assertNotNull(saved.getId());
        assertTrue(saved.getId() > 0);

        Ingredient reloaded = retriever.findIngredientById(saved.getId());
        assertNotNull(reloaded);
        assertEquals(1, reloaded.getStockMovements().size());
    }

    @Test
    void saveOrder_validOrderWithStock_savesSuccessfully() throws SQLException {
        Dish dish = retriever.findDishById(1);
        assertNotNull(dish);

        Order order = new Order();
        order.setReference("ORD-VALID-" + System.currentTimeMillis());
        order.setCreationDatetime(Instant.now());
        order.setTotalTtc(new BigDecimal("3500.00"));
        order.setPaymentStatus(PaymentStatusEnum.UNPAID);

        DishOrder line = new DishOrder();
        line.setDish(dish);
        line.setQuantity(1);
        order.addDishOrder(line);

        Order saved = retriever.saveOrder(order);
        assertNotNull(saved.getId());
        assertEquals(order.getReference(), saved.getReference());
        assertEquals(PaymentStatusEnum.UNPAID, saved.getPaymentStatus());
    }

    @Test
    void saveOrder_insufficientStock_throwsIllegalStateException() throws SQLException {
        Dish dish = retriever.findDishById(1);
        assertNotNull(dish);

        Order order = new Order();
        order.setReference("ORD-INSUFF-" + System.currentTimeMillis());
        order.setCreationDatetime(Instant.now());
        order.setTotalTtc(new BigDecimal("99999999.99"));

        DishOrder line = new DishOrder();
        line.setDish(dish);
        line.setQuantity(1000000);
        order.addDishOrder(line);

        IllegalStateException thrown = assertThrows(IllegalStateException.class,
                () -> retriever.saveOrder(order));
        assertTrue(thrown.getMessage().contains("Stock insuffisant"));
    }

    @Test
    void findOrderByReference_existingReference_returnsOrderWithLines() throws SQLException {
        String uniqueRef = "ORD-FIND-" + System.currentTimeMillis();

        Order order = new Order();
        order.setReference(uniqueRef);
        order.setCreationDatetime(Instant.now());
        order.setTotalTtc(new BigDecimal("3500.00"));
        order.setPaymentStatus(PaymentStatusEnum.UNPAID);

        Dish dish = retriever.findDishById(1);
        assertNotNull(dish);

        DishOrder line = new DishOrder();
        line.setDish(dish);
        line.setQuantity(1);
        order.addDishOrder(line);

        Order saved = retriever.saveOrder(order);

        Order reloaded = retriever.findOrderByReference(uniqueRef);
        assertNotNull(reloaded);
        assertEquals(uniqueRef, reloaded.getReference());
        assertEquals(PaymentStatusEnum.UNPAID, reloaded.getPaymentStatus());
        assertFalse(reloaded.getDishOrders().isEmpty());
    }

    @Test
    void findOrderByReference_nonExisting_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> retriever.findOrderByReference("ORD-FAKE-999999"));
    }

    @Test
    void createSaleFrom_paidOrder_createsSaleSuccessfully() throws SQLException {
        String uniqueRef = "ORD-SALE-" + System.currentTimeMillis();

        Order order = new Order();
        order.setReference(uniqueRef);
        order.setCreationDatetime(Instant.now());
        order.setTotalTtc(new BigDecimal("3500.00"));
        order.setPaymentStatus(PaymentStatusEnum.PAID);

        Dish dish = retriever.findDishById(1);
        assertNotNull(dish);

        DishOrder line = new DishOrder();
        line.setDish(dish);
        line.setQuantity(1);
        order.addDishOrder(line);

        Order savedOrder = retriever.saveOrder(order);

        Sale sale = retriever.createSaleFrom(savedOrder);
        assertNotNull(sale.getId());
        assertEquals(savedOrder, sale.getOrder());
        assertNotNull(sale.getCreationDatetime());
    }

    @Test
    void createSaleFrom_unpaidOrder_throwsIllegalStateException() throws SQLException {
        String uniqueRef = "ORD-UNPAID-" + System.currentTimeMillis();

        Order order = new Order();
        order.setReference(uniqueRef);
        order.setCreationDatetime(Instant.now());
        order.setTotalTtc(new BigDecimal("3500.00"));
        order.setPaymentStatus(PaymentStatusEnum.UNPAID);

        Dish dish = retriever.findDishById(1);
        DishOrder line = new DishOrder();
        line.setDish(dish);
        line.setQuantity(1);
        order.addDishOrder(line);

        Order savedOrder = retriever.saveOrder(order);

        assertThrows(IllegalStateException.class,
                () -> retriever.createSaleFrom(savedOrder));
    }

    @Test
    void markOrderAsPaid_existingUnpaidOrder_updatesToPaid() throws SQLException {
        String uniqueRef = "ORD-MARK-" + System.currentTimeMillis();

        Order order = new Order();
        order.setReference(uniqueRef);
        order.setCreationDatetime(Instant.now());
        order.setTotalTtc(new BigDecimal("3500.00"));
        order.setPaymentStatus(PaymentStatusEnum.UNPAID);

        Dish dish = retriever.findDishById(1);
        DishOrder line = new DishOrder();
        line.setDish(dish);
        line.setQuantity(1);
        order.addDishOrder(line);

        Order savedOrder = retriever.saveOrder(order);

        dr.markOrderAsPaid(uniqueRef);

        Order reloaded = dr.findOrderByReference(uniqueRef);
        assertEquals(PaymentStatusEnum.PAID, reloaded.getPaymentStatus());
    }

    @Test
    void markOrderAsPaid_alreadyPaid_throwsIllegalStateException() throws SQLException {
        String uniqueRef = "ORD-ALREADY-" + System.currentTimeMillis();

        Order order = new Order();
        order.setReference(uniqueRef);
        order.setCreationDatetime(Instant.now());
        order.setTotalTtc(new BigDecimal("3500.00"));
        order.setPaymentStatus(PaymentStatusEnum.PAID);

        Dish dish = retriever.findDishById(1);
        DishOrder line = new DishOrder();
        line.setDish(dish);
        line.setQuantity(1);
        order.addDishOrder(line);

        Order savedOrder = retriever.saveOrder(order);

        assertThrows(IllegalStateException.class,
                () -> dr.markOrderAsPaid(uniqueRef));
    }
}