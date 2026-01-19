import java.util.ArrayList;
import java.util.List;

public class Dish {
    private Integer id;
    private String name;
    private DishTypeEnum dishType;
    private Double sellingPrice;
    private List<Ingredient> ingredients = new ArrayList<>();

    public Dish() {}

    public Dish(Integer id, String name, DishTypeEnum dishType, Double sellingPrice) {
        this.id = id;
        this.name = name;
        this.dishType = dishType;
        this.sellingPrice = sellingPrice;
    }

    public void addIngredient(Ingredient ingredient) {
        ingredients.add(ingredient);
    }

    public double getCostPrice() {
        return ingredients.stream()
                .mapToDouble(Ingredient::calculateCost)
                .sum();
    }

    public double getMargin() {
        if (sellingPrice == null || sellingPrice <= 0) return 0;
        return sellingPrice - getCostPrice();
    }

    public double getMarginPercent() {
        double margin = getMargin();
        return sellingPrice != null && sellingPrice > 0
                ? (margin / sellingPrice) * 100
                : 0;
    }

    // Getters & Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public DishTypeEnum getDishType() { return dishType; }
    public Double getSellingPrice() { return sellingPrice; }
    public List<Ingredient> getIngredients() { return new ArrayList<>(ingredients); }

    @Override
    public String toString() {
        double cost = getCostPrice();
        double margin = getMargin();
        double marginPct = getMarginPercent();

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Plat : %s (%s)\n", name, dishType));
        sb.append(String.format("Coût matière     : %,8.0f Ar\n", cost));
        sb.append(String.format("Prix de vente    : %,8.0f Ar\n", sellingPrice != null ? sellingPrice : 0));
        sb.append(String.format("Marge brute      : %,8.0f Ar (%.1f%%)\n", margin, marginPct));
        sb.append("Ingrédients :\n");

        if (ingredients.isEmpty()) {
            sb.append("  (aucun ingrédient)\n");
        } else {
            ingredients.forEach(ing ->
                    sb.append("  • ").append(ing.toString()).append("\n")
            );
        }

        return sb.toString();
    }
}
