import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

class DishDataRetrieverTest {

    private DishDataRetriever retriever;

    @BeforeEach
    void setUp() {
        try {
            retriever = new DishDataRetriever();
        } catch (SQLException e) {
            // On échoue explicitement mais avec message clair
            fail("Échec connexion BDD : " + e.getMessage());
        }
    }

    @Test
    void testFindDishById_1() throws SQLException {
        Dish d = retriever.findDishById(1);
        assertNotNull(d);
        assertEquals(1, d.getId());
        assertTrue(d.getName().toLowerCase().contains("salade"));
    }

    @Test
    void testFindDishById_9999_returnsNull() throws SQLException {
        assertNull(retriever.findDishById(9999));
    }

    @Test
    void testGetDishCost_1() throws SQLException {
        BigDecimal cost = retriever.getDishCost(1);
        assertTrue(cost.compareTo(BigDecimal.ZERO) >= 0);
    }

    @Test
    void testGetGrossMargin_2() throws SQLException {
        BigDecimal margin = retriever.getGrossMargin(2);
        assertTrue(margin.compareTo(BigDecimal.ZERO) >= 0);
    }

    @Test
    void testGetGrossMargin_withoutPrice_throwsException() throws SQLException {
        assertThrows(IllegalStateException.class,
                () -> retriever.getGrossMargin(3));
    }
}
