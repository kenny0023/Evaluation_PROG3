import java.math.BigDecimal;

public class StockValue {
    private BigDecimal quantity;
    private UnitTypeEnum unit;

    public StockValue(BigDecimal quantity, UnitTypeEnum unit) {
        this.quantity = quantity != null ? quantity : BigDecimal.ZERO;
        this.unit = unit != null ? unit : UnitTypeEnum.KG;
    }

    public BigDecimal getQuantity() { return quantity; }
    public UnitTypeEnum getUnit() { return unit; }

    @Override
    public String toString() {
        return String.format("%s %s", quantity.setScale(2), unit);
    }
}
