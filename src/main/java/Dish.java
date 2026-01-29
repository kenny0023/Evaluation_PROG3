import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class Dish {
    private Integer id;
    private String name;
    private DishTypeEnum dishType;
    private BigDecimal sellingPrice;
    private List<DishIngredient> dishIngredients = new ArrayList<>();

    public Dish() {}

    public void addIngredient(Ingredient ingredient, BigDecimal quantity, UnitTypeEnum unit) {
        dishIngredients.add(new DishIngredient(this, ingredient, quantity, unit));
    }

    public BigDecimal getDishCost() {
        return dishIngredients.stream()
                .map(DishIngredient::calculateCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getGrossMargin() {
        if (sellingPrice == null) {
            throw new IllegalStateException("Prix de vente non défini pour : " + name);
        }
        return sellingPrice.subtract(getDishCost()).setScale(2, RoundingMode.HALF_UP);
    }

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
        sb.append(String.format("Coût matière   : %10.2f Ar\n", getDishCost()));
        sb.append(String.format("Prix de vente  : %10.2f Ar\n", sellingPrice != null ? sellingPrice : BigDecimal.ZERO));

        if (sellingPrice != null) {
            BigDecimal margin = getGrossMargin();
            double percent = sellingPrice.compareTo(BigDecimal.ZERO) > 0
                    ? margin.doubleValue() / sellingPrice.doubleValue() * 100
                    : 0;
            sb.append(String.format("Marge brute    : %10.2f Ar (%.1f%%)\n", margin, percent));
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
