import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DataRetrieverTest {

    private DataRetriever retriever;
    private int ingredientCreeId = -1;

    @BeforeEach
    void setUp() {
        retriever = new DataRetriever();
    }

    @Test
    @Order(1)
    @DisplayName("6-a : findDishById retourne un plat complet avec prix calculé")
    void testFindDishById() {
        Dish dish = retriever.findDishById(4);

        assertNotNull(dish, "Le plat avec ID 4 doit exister");
        assertEquals("Gâteau au chocolat", dish.getName());
        assertEquals("DESSERT", dish.getDishType());
        assertTrue(dish.getPrice() >= 5500.0, "Le prix doit être au moins 5500 (Chocolat + Beurre)");
        assertEquals(dish.getPrice(), dish.getDishCost(),
                "getDishCost() doit retourner le même prix que l'attribut price");
    }

    @Test
    @Order(2)
    @DisplayName("Pagination des ingrédients fonctionne")
    void testFindAllIngredients() {
        List<Ingredient> page1 = retriever.findAllIngredients(1, 3);

        assertFalse(page1.isEmpty(), "La première page ne doit pas être vide");
        assertTrue(page1.size() <= 3, "La taille de page doit être respectée");
        assertNotNull(page1.get(0).getName(), "Les ingrédients doivent avoir un nom");
    }

    @Test
    @Order(3)
    @DisplayName("Filtre par catégorie VEGETABLE retourne uniquement des légumes")
    void testFindIngredientsByCategory() {
        List<Ingredient> legumes = retriever.findIngredientsByCategory(CategoryEnum.VEGETABLE);

        assertFalse(legumes.isEmpty(), "Il doit y avoir au moins Laitue et Tomate");
        assertTrue(legumes.stream()
                        .allMatch(ing -> ing.getCategory() == CategoryEnum.VEGETABLE),
                "Tous les ingrédients retournés doivent être VEGETABLE");
    }

    @Test
    @Order(4)
    @DisplayName("Recherche par nom trouve 'Chocolat'")
    void testFindIngredientsByName() {
        List<Ingredient> resultats = retriever.findIngredientsByName("choco");

        assertFalse(resultats.isEmpty(), "Doit trouver l'ingrédient 'Chocolat'");
        assertTrue(resultats.stream()
                        .anyMatch(ing -> ing.getName().toLowerCase().contains("chocolat")),
                "Au moins un résultat doit contenir 'chocolat'");
    }

    @Test
    @Order(5)
    @DisplayName("CRUD : Création d'un ingrédient + impact sur le prix du plat")
    void testCreateIngredient() {
        Dish gateau = retriever.findDishById(4);
        assertNotNull(gateau);

        double prixAvant = gateau.getPrice();

        Ingredient oeuf = new Ingredient();
        oeuf.setName("Œuf bio (test unitaire)");
        oeuf.setPrice(800.00);
        oeuf.setCategory(CategoryEnum.ANIMAL);
        oeuf.setDish(gateau);

        boolean succes = retriever.createIngredient(oeuf);

        assertTrue(succes, "La création de l'ingrédient doit réussir");
        assertTrue(oeuf.getId() > 0, "Un ID doit être généré par la base");

        ingredientCreeId = oeuf.getId();

        double prixApres = retriever.recalculateDishPrice(4);
        assertEquals(prixAvant + 800.00, prixApres, 0.01,
                "Le prix du plat doit augmenter de 800 FCFA après ajout de l'œuf");
    }

    @Test
    @Order(6)
    @DisplayName("CRUD : Mise à jour d'un ingrédient modifie le prix du plat")
    void testUpdateIngredient() {
        assumeTrue(ingredientCreeId > 0, "Un ingrédient doit avoir été créé avant");

        double prixAvant = retriever.recalculateDishPrice(4);

        Ingredient oeuf = new Ingredient();
        oeuf.setId(ingredientCreeId);
        oeuf.setName("Œuf extra frais");
        oeuf.setPrice(1200.00); // Augmentation de 400
        oeuf.setCategory(CategoryEnum.ANIMAL);
        oeuf.setDish(retriever.findDishById(4));

        boolean succes = retriever.updateIngredient(oeuf);

        assertTrue(succes, "La mise à jour doit réussir");

        double prixApres = retriever.recalculateDishPrice(4);
        assertEquals(prixAvant + 400.00, prixApres, 0.01,
                "Le prix du plat doit augmenter de 400 FCFA après modification");
    }

    @Test
    @Order(7)
    @DisplayName("CRUD : Suppression d'un ingrédient restaure le prix initial")
    void testDeleteIngredient() {
        assumeTrue(ingredientCreeId > 0, "Un ingrédient doit exister pour être supprimé");

        double prixAvantSuppression = retriever.recalculateDishPrice(4);

        boolean succes = retriever.deleteIngredient(ingredientCreeId);

        assertTrue(succes, "La suppression doit réussir");

        double prixFinal = retriever.recalculateDishPrice(4);
        assertEquals(5500.0, prixFinal, 0.01,
                "Le prix du plat doit revenir à 5500 FCFA après suppression de l'ingrédient test");
    }
}