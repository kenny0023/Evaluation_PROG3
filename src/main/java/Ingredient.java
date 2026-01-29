import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Ingredient {
    private Integer id;
    private String name;
    private BigDecimal price;
    private CategoryEnum category;
    private List<StockMovement> stockMovements = new ArrayList<>();

    public Ingredient() {}

    public Ingredient(Integer id, String name, BigDecimal price, CategoryEnum category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public CategoryEnum getCategory() { return category; }
    public void setCategory(CategoryEnum category) { this.category = category; }

    public List<StockMovement> getStockMovements() {
        return new ArrayList<>(stockMovements);
    }

    public void setStockMovements(List<StockMovement> movements) {
        this.stockMovements = new ArrayList<>(movements);
    }

    public void addStockMovement(StockMovement movement) {
        if (movement != null) {
            movement.setIngredientId(this.id);
            stockMovements.add(movement);
        }
    }

    public StockValue getStockValueAt(Instant t) {
        if (t == null) {
            throw new IllegalArgumentException("Instant t cannot be null");
        }

        BigDecimal total = BigDecimal.ZERO;
        UnitTypeEnum refUnit = null;

        for (StockMovement m : stockMovements) {
            if (m.getCreationDatetime() != null && !m.getCreationDatetime().isAfter(t)) {
                if (refUnit == null) {
                    refUnit = m.getUnit();
                } else if (refUnit != m.getUnit()) {
                    throw new IllegalStateException("Mixed units in stock movements for " + name);
                }

                if (m.getType() == MovementTypeEnum.IN) {
                    total = total.add(m.getQuantity());
                } else {
                    total = total.subtract(m.getQuantity());
                }
            }
        }

        return new StockValue(total, refUnit != null ? refUnit : UnitTypeEnum.KG);
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", category=" + category +
                ", movements=" + stockMovements.size() +
                '}';
    }
}
