import java.util.ArrayList;
import java.util.List;

public class Dish {
    private int id;
    private String name;
    private DishTypeEnum dishType;
    private List<Ingredient> ingredients = new ArrayList<>();

    public Dish(int id, String name, DishTypeEnum dishType) {
        this.id = id;
        this.name = name;
        this.dishType = dishType;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public DishTypeEnum getDishType() { return dishType; }
    public List<Ingredient> getIngredients() { return ingredients; }

    public void addIngredient(Ingredient ingredient) {
        this.ingredients.add(ingredient);
    }

    public double getDishPrice() {
        double total = 0.0;
        for (Ingredient ing : ingredients) {
            total += ing.getTotalPrice();
        }
        return total;
    }

    @Override
    public String toString() {
        return String.format("Dish{id=%d, name='%s', dishType=%s, prixTotal=%.2f €, ingrédients=%d}",
                id, name, dishType, getDishPrice(), ingredients.size());
    }
}
