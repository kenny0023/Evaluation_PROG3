//import java.math.BigDecimal;
//import java.time.Instant;
//
//public class StockMovement {
//    private Integer id;
//    private Integer ingredientId;
//    private BigDecimal quantity;               // ← BigDecimal
//    private UnitTypeEnum unit;
//    private MovementTypeEnum type;
//    private Instant creationDatetime;
//
//    public StockMovement() {}
//
//    // Getters & Setters
//    public Integer getId() { return id; }
//    public void setId(Integer id) { this.id = id; }
//    public Integer getIngredientId() { return ingredientId; }
//    public void setIngredientId(Integer ingredientId) { this.ingredientId = ingredientId; }
//    public BigDecimal getQuantity() { return quantity; }
//    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
//    public UnitTypeEnum getUnit() { return unit; }
//    public void setUnit(UnitTypeEnum unit) { this.unit = unit; }
//    public MovementTypeEnum getType() { return type; }
//    public void setType(MovementTypeEnum type) { this.type = type; }
//    public Instant getCreationDatetime() { return creationDatetime; }
//    public void setCreationDatetime(Instant creationDatetime) { this.creationDatetime = creationDatetime; }
//
//    @Override
//    public String toString() {
//        return "StockMovement{" +
//                "id=" + id +
//                ", ingredientId=" + ingredientId +
//                ", quantity=" + quantity +
//                ", unit=" + unit +
//                ", type=" + type +
//                ", creationDatetime=" + creationDatetime +
//                '}';
//    }
//}


import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public class StockMovement {
    private Integer id;
    private Integer ingredientId;
    private BigDecimal quantity;
    private UnitTypeEnum unit;
    private MovementTypeEnum type;
    private Instant creationDatetime;

    public StockMovement() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getIngredientId() { return ingredientId; }
    public void setIngredientId(Integer ingredientId) { this.ingredientId = ingredientId; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public UnitTypeEnum getUnit() { return unit; }
    public void setUnit(UnitTypeEnum unit) { this.unit = unit; }

    public MovementTypeEnum getType() { return type; }
    public void setType(MovementTypeEnum type) { this.type = type; }

    public Instant getCreationDatetime() { return creationDatetime; }
    public void setCreationDatetime(Instant creationDatetime) { this.creationDatetime = creationDatetime; }

    @Override
    public String toString() {
        return "StockMovement{" +
                "id=" + id +
                ", ingredientId=" + ingredientId +
                ", quantity=" + quantity +
                ", unit=" + unit +
                ", type=" + type +
                ", creationDatetime=" + creationDatetime +
                '}';
    }
}