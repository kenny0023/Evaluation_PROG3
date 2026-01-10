import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DishDataRetrieverTest {

    private DishDataRetriever retriever;

    @BeforeEach
    void setUp() throws Exception {
        retriever = new DishDataRetriever();

        try (Connection conn = new DBConnection().getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("DELETE FROM Ingredient;");
            stmt.executeUpdate("DELETE FROM Dish;");

            stmt.executeUpdate("ALTER SEQUENCE dish_id_seq RESTART WITH 1;");
            stmt.executeUpdate("ALTER SEQUENCE ingredient_id_seq RESTART WITH 1;");

            stmt.executeUpdate("""
                INSERT INTO Dish (name, dish_type) VALUES
                ('Salade fraîche', 'START'),
                ('Poulet grillé', 'MAIN'),
                ('Riz aux légumes', 'MAIN'),
                ('Gâteau au chocolat', 'DESSERT'),
                ('Salade de fruits', 'DESSERT')
                """);

            stmt.executeUpdate("""
                INSERT INTO Ingredient (name, price, category, id_dish) VALUES
                ('Laitue', 800.00, 'VEGETABLE', 1),
                ('Tomate', 600.00, 'VEGETABLE', 1),
                ('Poulet', 4500.00, 'ANIMAL', 2),
                ('Chocolat', 3000.00, 'OTHER', 4),
                ('Beurre', 2500.00, 'DAIRY', 4)
                """);
        }
    }

    @Test
    void testFindDishById_ReturnsCorrectDish() throws Exception {
        Dish dish = retriever.findDishById(1);

        assertNotNull(dish);
        assertEquals("Salade fraîche", dish.getName());
        assertEquals(DishTypeEnum.START, dish.getDishType());
        assertEquals(2, dish.getIngredients().size());
    }

    @Test
    void testGetDishPrice_CalculatesCorrectly_WhenQuantitiesAreSet() throws Exception {
        Dish dish = retriever.findDishById(1);

        dish.getIngredients().get(0).setQuantity(1.0);
        dish.getIngredients().get(1).setQuantity(2.0);

        double price = dish.getDishPrice();
        assertEquals(2000.0, price, 0.01);
    }

    @Test
    void testSaveDish_CreatesNewDish_WithIngredients() throws Exception {
        Dish newDish = new Dish(0, "Crêpe Suzette", DishTypeEnum.DESSERT);

        Ingredient farine = new Ingredient(
                0, "Farine", 500.0, CategoryEnum.VEGETABLE, newDish, 0.25);
        Ingredient oeuf = new Ingredient(
                0, "Œuf", 300.0, CategoryEnum.ANIMAL, newDish, 2.0);

        newDish.addIngredient(farine);
        newDish.addIngredient(oeuf);

        retriever.saveDish(newDish);

        assertTrue(newDish.getId() > 0);

        Dish saved = retriever.findDishById(newDish.getId());
        assertNotNull(saved);
        assertEquals("Crêpe Suzette", saved.getName());
        assertEquals(2, saved.getIngredients().size());

        assertEquals(725.0, saved.getDishPrice(), 0.01);
    }

    @Test
    void testFindDishByIdWithPrice_ShowsTotal_WhenQuantitiesSet() throws Exception {
        Dish dish = retriever.findDishById(4);

        dish.getIngredients().get(0).setQuantity(0.2);
        dish.getIngredients().get(1).setQuantity(0.1);

        retriever.findDishByIdWithPrice(4);

        assertEquals(850.0, dish.getDishPrice(), 0.01);
    }

    @Test
    void testFindIngredients_Pagination() throws Exception {
        List<Ingredient> list = retriever.findIngredients(1, 3);

        assertEquals(3, list.size());
        assertEquals("Laitue", list.get(0).getName());
    }

    @Test
    void testFindIngredientsByCategory() throws Exception {
        List<Ingredient> veggies =
                retriever.findIngredientsByCategory(CategoryEnum.VEGETABLE);

        assertEquals(2, veggies.size());
        assertTrue(veggies.stream()
                .allMatch(i -> i.getCategory() == CategoryEnum.VEGETABLE));
    }
}

