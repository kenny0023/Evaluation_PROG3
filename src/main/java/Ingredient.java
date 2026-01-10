public class Ingredient {
    private int id;
    private String name;
    private double price;
    private CategoryEnum category;
    private Dish dish;
    private Double quantity;

    public Ingredient(int id, String name, double price, CategoryEnum category, Dish dish) {
        this(id, name, price, category, dish, null);
    }

    public Ingredient(int id, String name, double price, CategoryEnum category, Dish dish, Double quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.dish = dish;
        this.quantity = quantity;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public CategoryEnum getCategory() { return category; }
    public Dish getDish() { return dish; }
    public String getDishName() { return dish != null ? dish.getName() : "aucun"; }

    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }

    public double getTotalPrice() {
        if (quantity == null) {
            throw new IllegalStateException("La quantité de l'ingrédient '" + name + "' n'a pas encore été fixée.");
        }
        return price * quantity;
    }

    @Override
    public String toString() {
        return "Ingredient{id=" + id +
                ", name='" + name +
                "', price=" + price +
                ", quantity=" + quantity +
                ", category=" + category +
                ", dish='" + getDishName() + "'}";
    }
}
