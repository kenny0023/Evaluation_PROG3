//import java.math.BigDecimal;
//
//public class DishIngredient {
//    private Dish dish;
//    private Ingredient ingredient;
//    private BigDecimal quantityRequired;
//    private UnitTypeEnum unit;
//
//    public DishIngredient() {}
//
//    public DishIngredient(Dish dish, Ingredient ingredient, BigDecimal quantityRequired, UnitTypeEnum unit) {
//        this.dish = dish;
//        this.ingredient = ingredient;
//        this.quantityRequired = quantityRequired;
//        this.unit = unit;
//    }
//
//    public BigDecimal calculateCost() {
//        if (ingredient == null || ingredient.getPrice() == null || quantityRequired == null) {
//            return BigDecimal.ZERO;
//        }
//        return ingredient.getPrice().multiply(quantityRequired);
//    }
//
//    // Getters & Setters
//    public Dish getDish() { return dish; }
//    public void setDish(Dish dish) { this.dish = dish; }
//    public Ingredient getIngredient() { return ingredient; }
//    public void setIngredient(Ingredient ingredient) { this.ingredient = ingredient; }
//    public BigDecimal getQuantityRequired() { return quantityRequired; }
//    public void setQuantityRequired(BigDecimal quantityRequired) { this.quantityRequired = quantityRequired; }
//    public UnitTypeEnum getUnit() { return unit; }
//    public void setUnit(UnitTypeEnum unit) { this.unit = unit; }
//
//    @Override
//    public String toString() {
//        return String.format("%.2f %s de %s", quantityRequired, unit, ingredient.getName());
//    }
//}

import java.math.BigDecimal;

public class DishIngredient {
    private Dish dish;
    private Ingredient ingredient;
    private BigDecimal quantityRequired;  // ← BigDecimal au lieu de Double
    private UnitTypeEnum unit;  // ou UnitTypeEnum selon ton enum

    public DishIngredient() {}

    public DishIngredient(Dish dish, Ingredient ingredient, BigDecimal quantityRequired, UnitTypeEnum unit) {
        this.dish = dish;
        this.ingredient = ingredient;
        this.quantityRequired = quantityRequired;
        this.unit = unit;
    }

    /**
     * Calcule le coût de cet ingrédient pour le plat
     */
    public BigDecimal calculateCost() {
        if (ingredient == null || ingredient.getPrice() == null || quantityRequired == null) {
            return BigDecimal.ZERO;
        }
        return ingredient.getPrice().multiply(quantityRequired);
    }

    // Getters et Setters
    public Dish getDish() { return dish; }
    public void setDish(Dish dish) { this.dish = dish; }

    public Ingredient getIngredient() { return ingredient; }
    public void setIngredient(Ingredient ingredient) { this.ingredient = ingredient; }

    public BigDecimal getQuantityRequired() { return quantityRequired; }
    public void setQuantityRequired(BigDecimal quantityRequired) { this.quantityRequired = quantityRequired; }

    public UnitTypeEnum getUnit() { return unit; }
    public void setUnit(UnitTypeEnum unit) { this.unit = unit; }

    @Override
    public String toString() {
        return String.format("%.2f %s de %s", quantityRequired, unit, ingredient != null ? ingredient.getName() : "null");
    }
}