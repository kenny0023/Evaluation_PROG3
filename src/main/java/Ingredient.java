public class Ingredient {
    private Integer id;
    private String name;
    private Double price;
    private CategoryEnum category;

    // Ces deux champs ne sont pertinents QUE dans le contexte d'un plat
    private Double requiredQuantity;   // quantité utilisée dans ce plat
    private String unit;               // ou UnitEnum si tu veux être plus strict

    // Constructeurs
    public Ingredient() {}

    // Utilisé lors de la lecture depuis la BDD (avec quantité/unité)
    public Ingredient(Integer id, String name, Double price, CategoryEnum category,
                      Double requiredQuantity, String unit) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.requiredQuantity = requiredQuantity;
        this.unit = unit;
    }

    // Utilisé quand on crée un ingrédient seul (sans quantité/unité)
    public Ingredient(String name, Double price, CategoryEnum category) {
        this.name = name;
        this.price = price;
        this.category = category;
    }

    public double calculateCost() {
        if (requiredQuantity == null) {
            throw new IllegalStateException("Quantité requise non définie pour l'ingrédient : " + name);
        }
        return price * requiredQuantity;
    }

    // Getters & Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public Double getPrice() { return price; }
    public CategoryEnum getCategory() { return category; }
    public Double getRequiredQuantity() { return requiredQuantity; }
    public void setRequiredQuantity(Double qty) { this.requiredQuantity = qty; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    @Override
    public String toString() {
        String qty = (requiredQuantity != null && unit != null)
                ? String.format("x %.2f %s", requiredQuantity, unit)
                : "";
        return String.format("%s %s (%.0f Ar)", name, qty, price);
    }
}
