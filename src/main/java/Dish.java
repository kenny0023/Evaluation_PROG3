import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Dish {
    private Integer id;
    private String name;
    private DishTypeEnum dishType;
    private BigDecimal sellingPrice;
    private List<DishIngredient> dishIngredients = new ArrayList<>();

    public Dish() {}

    public void addIngredient(Ingredient ingredient, BigDecimal quantityRequired, UnitTypeEnum unit) {
        DishIngredient di = new DishIngredient(this, ingredient, quantityRequired, unit);
        dishIngredients.add(di);
    }

    public BigDecimal getDishCost() {
        return dishIngredients.stream()
                .map(DishIngredient::calculateCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getGrossMargin() {
        if (sellingPrice == null) {
            throw new IllegalStateException("Prix de vente non défini pour " + name);
        }
        return sellingPrice.subtract(getDishCost());
    }

    // Getters & Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public DishTypeEnum getDishType() { return dishType; }
    public void setDishType(DishTypeEnum dishType) { this.dishType = dishType; }
    public BigDecimal getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(BigDecimal sellingPrice) { this.sellingPrice = sellingPrice; }
    public List<DishIngredient> getDishIngredients() { return new ArrayList<>(dishIngredients); }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("🍽 %s (%s)\n", name, dishType));
        sb.append(String.format("Coût matière   : %10.0f Ar\n", getDishCost()));
        sb.append(String.format("Prix de vente  : %10.0f Ar\n", sellingPrice != null ? sellingPrice : 0));

        if (sellingPrice != null) {
            BigDecimal margin = getGrossMargin();
            double percent = sellingPrice.compareTo(BigDecimal.ZERO) > 0
                    ? margin.doubleValue() / sellingPrice.doubleValue() * 100
                    : 0;
            sb.append(String.format("Marge brute    : %10.0f Ar (%.1f%%)\n", margin, percent));
        }

        sb.append("Ingrédients:\n");
        if (dishIngredients.isEmpty()) {
            sb.append("   (aucun)\n");
        } else {
            dishIngredients.forEach(di -> sb.append("   • ").append(di).append("\n"));
        }
        return sb.toString();
    }
}