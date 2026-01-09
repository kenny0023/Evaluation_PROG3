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
    void a_findDishById_WithIngredients() throws Exception {
        Dish dish = retriever.findDishById(1);

        assertNotNull(dish);
        assertEquals("Salade fraîche", dish.getName());
        assertEquals(DishTypeEnum.START, dish.getDishType());
        assertEquals(2, dish.getIngredients().size());
        assertEquals(1400.0, dish.getDishPrice(), 0.01);

        assertTrue(dish.getIngredients().stream().anyMatch(i -> i.getName().equals("Laitue")));
        assertTrue(dish.getIngredients().stream().anyMatch(i -> i.getName().equals("Tomate")));
    }

    @Test
    void b_findIngredients_Pagination() throws Exception {
        List<Ingredient> list = retriever.findIngredients(1, 3);

        assertEquals(3, list.size());
        assertEquals("Laitue", list.get(0).getName());
        assertEquals("Tomate", list.get(1).getName());
        assertEquals("Poulet", list.get(2).getName());
    }

    @Test
    void c_findIngredientsLike() throws Exception {
        List<Ingredient> list = retriever.findIngredientsLike("cho");

        assertEquals(1, list.size());
        assertEquals("Chocolat", list.get(0).getName());
    }

    @Test
    void f_findIngredientsByCategory() throws Exception {
        List<Ingredient> list = retriever.findIngredientsByCategory(CategoryEnum.VEGETABLE);

        assertEquals(2, list.size());
        assertTrue(list.stream().allMatch(i -> i.getCategory() == CategoryEnum.VEGETABLE));
    }

    @Test
    void h_findIngredientsByNameStartingWith() throws Exception {
        List<Ingredient> list = retriever.findIngredientsByNameStartingWith("Cho");

        assertEquals(1, list.size());
        assertEquals("Chocolat", list.get(0).getName());
    }

    @Test
    void i_createIngredient() throws Exception {
        Dish gateau = retriever.findDishById(4);
        Ingredient creme = new Ingredient(0, "Crème chantilly", 2000.00, CategoryEnum.DAIRY, gateau);

        retriever.createIngredient(creme);

        List<Ingredient> dairy = retriever.findIngredientsByCategory(CategoryEnum.DAIRY);
        assertTrue(dairy.stream().anyMatch(i -> i.getName().equals("Crème chantilly")));
    }

    @Test
    void k_saveDish() throws Exception {
        retriever.saveDish("Pizza Margherita", DishTypeEnum.MAIN);

        Dish pizza = retriever.findDishById(6);
        assertNotNull(pizza);
        assertEquals("Pizza Margherita", pizza.getName());
        assertEquals(DishTypeEnum.MAIN, pizza.getDishType());
    }

    @Test
    void testFindDishById_NotFound() throws Exception {
        Dish dish = retriever.findDishById(999);
        assertNull(dish);
    }
}
