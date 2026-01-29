import java.time.Instant;

public class StockMovement {
    private Integer id;
    private Integer ingredientId;         // id_ingredient
    private double quantity;
    private UnitTypeEnum unit;
    private MovementTypeEnum type;        // IN ou OUT
    private Instant creationDatetime;

    // Constructeurs
    public StockMovement() {}

    // Getters & Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getIngredientId() { return ingredientId; }
    public void setIngredientId(Integer ingredientId) { this.ingredientId = ingredientId; }
    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }
    public UnitTypeEnum getUnit() { return unit; }
    public void setUnit(UnitTypeEnum unit) { this.unit = unit; }
    public MovementTypeEnum getType() { return type; }
    public void setType(MovementTypeEnum type) { this.type = type; }
    public Instant getCreationDatetime() { return creationDatetime; }
    public void setCreationDatetime(Instant creationDatetime) { this.creationDatetime = creationDatetime; }

    @Override
    public String toString() {
        return String.format("StockMovement{id=%d, ing=%d, qty=%.2f %s, %s, %s}",
                id, ingredientId, quantity, unit, type, creationDatetime);
    }
}
