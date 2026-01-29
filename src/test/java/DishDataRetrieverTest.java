import org.junit.jupiter.api.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
        dr = retriever;
    }

    @AfterAll
    void tearDown() throws SQLException {
        if (retriever != null) {
            retriever.close();
        }
    }

    private void assertBigDecimalEquals(String expected, BigDecimal actual, String message) {
        assertNotNull(actual, message + " (la valeur est nulle)");
        BigDecimal exp = new BigDecimal(expected).setScale(2, RoundingMode.HALF_UP);
        BigDecimal act = actual.setScale(2, RoundingMode.HALF_UP);
        assertEquals(exp, act, message);
    }

    @Test
    void getDishCost_existingDish_returnsCorrectCost() throws SQLException {
        BigDecimal cost = retriever.getDishCost(1);
        assertBigDecimalEquals("250.00", cost, "Le coût du plat ID 1 est incorrect");
    }

    @Test
    void getGrossMargin_existingDishWithPrice_returnsCorrectMargin() throws SQLException {
        BigDecimal margin = retriever.getGrossMargin(1);
        assertBigDecimalEquals("3250.00", margin, "La marge brute du plat ID 1 est incorrecte");
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
        assertFalse(ing.getStockMovements().isEmpty(), "L'ingrédient devrait avoir des mouvements de stock");

        Instant t = Instant.parse("2024-06-01T12:08:00Z");
        StockValue stock = ing.getStockValueAt(t);
        assertNotNull(stock);
        assertBigDecimalEquals("4.80", stock.getQuantity(), "La quantité en stock au temps T est incorrecte");
    }

    @Test
    void saveIngredient_newIngredientWithMovements_savesAndReturnsId() throws SQLException {
        String uniqueName = "TestIngr-" + System.currentTimeMillis();

        Ingredient newIng = new Ingredient();
        newIng.setName(uniqueName);
        newIng.setPrice(new BigDecimal("12000.00"));
        newIng.setCategory(CategoryEnum.OTHER);

        StockMovement sm = new StockMovement();
        sm.setQuantity(new BigDecimal("5.0"));
        sm.setUnit(UnitTypeEnum.KG);
        sm.setType(MovementTypeEnum.IN);
        sm.setCreationDatetime(Instant.now().minus(1, ChronoUnit.DAYS));

        newIng.addStockMovement(sm);

        Ingredient saved = retriever.saveIngredient(newIng);

        assertNotNull(saved.getId(), "L'ID sauvegardé ne devrait pas être nul");
        assertTrue(saved.getId() > 0);

        Ingredient reloaded = retriever.findIngredientById(saved.getId());
        assertNotNull(reloaded);
        assertEquals(1, reloaded.getStockMovements().size());
    }

    @Test
    void saveOrder_validOrderWithStock_savesSuccessfully() throws SQLException {
        Dish dish = retriever.findDishById(1);
        assertNotNull(dish, "Le plat ID 1 doit exister en base pour ce test");

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
    }

    @Test
    void saveOrder_insufficientStock_throwsIllegalStateException() throws SQLException {
        Dish dish = retriever.findDishById(1);
        assertNotNull(dish);

        Order order = new Order();
        order.setReference("ORD-INSUFF-" + System.currentTimeMillis());
        order.setCreationDatetime(Instant.now());
        order.setTotalTtc(new BigDecimal("999999.00"));

        DishOrder line = new DishOrder();
        line.setDish(dish);
        line.setQuantity(1000000);
        order.addDishOrder(line);

        assertThrows(IllegalStateException.class, () -> retriever.saveOrder(order),
                "Une IllegalStateException devrait être jetée pour stock insuffisant");
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
        DishOrder line = new DishOrder();
        line.setDish(dish);
        line.setQuantity(1);
        order.addDishOrder(line);

        retriever.saveOrder(order);

        Order reloaded = retriever.findOrderByReference(uniqueRef);
        assertNotNull(reloaded);
        assertEquals(uniqueRef, reloaded.getReference());
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
        DishOrder line = new DishOrder();
        line.setDish(dish);
        line.setQuantity(1);
        order.addDishOrder(line);

        Order savedOrder = retriever.saveOrder(order);

        Sale sale = retriever.createSaleFrom(savedOrder);
        assertNotNull(sale);
        assertNotNull(sale.getId());
        assertEquals(savedOrder.getReference(), sale.getOrder().getReference());
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

        assertThrows(IllegalStateException.class, () -> retriever.createSaleFrom(savedOrder));
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

        retriever.saveOrder(order);

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

        retriever.saveOrder(order);

        assertThrows(IllegalStateException.class, () -> dr.markOrderAsPaid(uniqueRef));
    }
}