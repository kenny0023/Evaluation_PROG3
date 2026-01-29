public class StockValue {
    private double quantity;
    private UnitTypeEnum unit;   // ou UnitEnum si tu as changé le nom

    public StockValue() {}

    public StockValue(double quantity, UnitTypeEnum unit) {
        this.quantity = quantity;
        this.unit = unit;
    }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }
    public UnitTypeEnum getUnit() { return unit; }
    public void setUnit(UnitTypeEnum unit) { this.unit = unit; }

    @Override
    public String toString() {
        return String.format("%.2f %s", quantity, unit);
    }
}
