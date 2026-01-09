public class Ingredient {
    private int id;
    private String name;
    private double price;
    private CategoryEnum category;
    private Dish dish;

    public Ingredient(int id, String name, double price, CategoryEnum category, Dish dish) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.dish = dish;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public CategoryEnum getCategory() { return category; }
    public Dish getDish() { return dish; }
    public String getDishName() { return dish != null ? dish.getName() : "aucun"; }

    @Override
    public String toString() {
        return "Ingredient{id=" + id + ", name='" + name + "', price=" + price +
                ", category=" + category + ", dish='" + getDishName() + "'}";
    }
}
