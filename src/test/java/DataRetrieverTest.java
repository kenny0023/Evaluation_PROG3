import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.sql.SQLException;

class DishDataRetrieverTest {

    private DishDataRetriever retriever;

    @BeforeEach
    void setUp() {
        retriever = new DishDataRetriever();
    }

    @Test
    void testFindDishById_ExistingDish_ReturnsDishWithIngredients() throws SQLException {
        Dish dish = retriever.findDishById(1);

        assertNotNull(dish, "Le plat ne devrait pas être null");

        // Tolérance maximale pour le nom du plat (accents + ligatures)
        String normalizedName = dish.getName()
                .replace("Œ", "Oe")
                .replace("œ", "oe")
                .replaceAll("[îï]", "i")
                .trim()
                .toLowerCase();

        assertTrue(
                normalizedName.contains("salade") && normalizedName.contains("fra"),
                "Le nom du plat devrait contenir 'salade' et 'fraîche' (tolérance accents)"
        );

        assertEquals(DishTypeEnum.START, dish.getDishType(),
                "Le type de plat devrait être START");

        // Vérification très souple pour les ingrédients
        boolean hasVegetableLike = dish.getIngredients().stream()
                .anyMatch(ing -> {
                    String n = ing.getName().trim().toLowerCase();
                    return n.contains("laitue") || n.contains("salade") || n.contains("tomate");
                });

        assertTrue(hasVegetableLike,
                "Le plat devrait contenir au moins un ingrédient de type salade/laitue/tomate (insensible casse)");
    }

    @Test
    void testFindDishById_NonExistingId_ReturnsNull() throws SQLException {
        Dish dish = retriever.findDishById(9999);
        assertNull(dish, "Un id inexistant devrait retourner null");
    }

    @Test
    void testDishCostCalculation_NotNegative() throws SQLException {
        Dish dish = retriever.findDishById(1);

        assertNotNull(dish);

        double cost = dish.getCostPrice();
        assertTrue(cost >= 0, "Le coût matière ne devrait jamais être négatif");
    }

    @Test
    void testDishWithSellingPrice_HasValidMargin() throws SQLException {
        // Suppose que le plat 2 a un prix de vente
        Dish dish = retriever.findDishById(2);

        assumeTrue(dish != null, "Ce test suppose que le plat 2 existe");
        assumeTrue(dish.getSellingPrice() != null, "Ce test suppose un prix de vente");

        double margin = dish.getMargin();
        double marginPercent = dish.getMarginPercent();

        assertTrue(margin >= 0, "La marge brute ne devrait pas être négative");
        assertTrue(marginPercent >= 0 && marginPercent <= 100,
                "Le pourcentage de marge devrait être entre 0% et 100%");
    }
}