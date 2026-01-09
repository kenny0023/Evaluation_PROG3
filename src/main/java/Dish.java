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
    public void addIngredient(Ingredient ingredient) { this.ingredients.add(ingredient); }

    public double getDishPrice() {
        return ingredients.stream().mapToDouble(Ingredient::getPrice).sum();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Dish{id=").append(id)
                .append(", name='").append(name)
                .append("', type=").append(dishType)
                .append(", prixTotal=").append(String.format("%.2f", getDishPrice()))
                .append(" €, ingrédients=").append(ingredients.size()).append("}\n");
        for (Ingredient i : ingredients) {
            sb.append("   → ").append(i).append("\n");
        }
        return sb.toString();
    }
}
