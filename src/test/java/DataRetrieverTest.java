import org.junit.jupiter.api.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DishDataRetrieverTest {

    private DishDataRetriever retriever;

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

    // ───────────────────────────────────────────────
    // TD3 - Coût d'un plat
    // ───────────────────────────────────────────────
    @Test
    void getDishCost_existingDish_returnsCorrectCost() throws SQLException {
        BigDecimal cost = retriever.getDishCost(1);
        assertEquals(new BigDecimal("250.00"), cost.setScale(2));
    }

    @Test
    void getDishCost_nonExistingDish_returnsZero() throws SQLException {
        BigDecimal cost = retriever.getDishCost(999);
        assertEquals(BigDecimal.ZERO.setScale(2), cost.setScale(2));
    }

    // ───────────────────────────────────────────────
    // TD3 - Marge brute
    // ───────────────────────────────────────────────
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
    void getGrossMargin_nonExistingDish_throwsSQLException() {
        assertThrows(SQLException.class, () -> retriever.getGrossMargin(999));
    }

    // ───────────────────────────────────────────────
    // TD4 - Stock d'un ingrédient
    // ───────────────────────────────────────────────
    @Test
    void findIngredientById_existingId_returnsIngredientWithMovements() throws SQLException {
        Ingredient ing = retriever.findIngredientById(1);
        assertNotNull(ing);
        assertEquals("Laitue", ing.getName());
        assertFalse(ing.getStockMovements().isEmpty());

        Instant t = Instant.parse("2024-06-01T12:08:00Z");
        StockValue stock = ing.getStockValueAt(t);

        assertEquals(new BigDecimal("4.80"), stock.getQuantity().setScale(2));
        assertEquals(UnitTypeEnum.KG, stock.getUnit());
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

        newIng.addStockMovement(new StockMovement() {{
            setQuantity(new BigDecimal("1.5"));
            setUnit(UnitTypeEnum.KG);
            setType(MovementTypeEnum.OUT);
            setCreationDatetime(Instant.now());
        }});

        Ingredient saved = retriever.saveIngredient(newIng);

        assertNotNull(saved.getId());
        assertTrue(saved.getId() > 0);

        Ingredient reloaded = retriever.findIngredientById(saved.getId());
        assertNotNull(reloaded);
        assertEquals(2, reloaded.getStockMovements().size());
    }

    // ───────────────────────────────────────────────
    // Annexe 2 - Commandes (version corrigée)
    // ───────────────────────────────────────────────
    @Test
    void saveOrder_validOrderWithStock_savesSuccessfully() throws SQLException {
        Dish dish = retriever.findDishById(1);
        assertNotNull(dish);

        Order order = new Order();
        order.setReference("ORD-VALID-" + System.currentTimeMillis());
        order.setCreationDatetime(Instant.now());
        order.setTotalTtc(new BigDecimal("3500.00"));

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
        order.setTotalTtc(new BigDecimal("99999999.99"));  // juste en dessous de 10^8

        DishOrder line = new DishOrder();
        line.setDish(dish);
        line.setQuantity(1000000);  // 1 million de plats
        order.addDishOrder(line);

        SQLException thrown = assertThrows(SQLException.class,
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
        assertFalse(reloaded.getDishOrders().isEmpty());
    }

    @Test
    void findOrderByReference_nonExisting_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> retriever.findOrderByReference("ORD-FAKE-999999"));
    }
}
