public class Dish {
    private int id;
    private String name;
    private String dishType;
    private double price;

    public Dish() {}

    public Dish(int id, String name, String dishType) {
        this.id = id;
        this.name = name;
        this.dishType = dishType;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDishType() { return dishType; }
    public void setDishType(String dishType) { this.dishType = dishType; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    @Override
    public String toString() {
        return "Dish : " +
                "id = " + id +
                ", name = '" + name + '\'' +
                ", dishType = '" + dishType + '\'' +
                ", price=" + price;
    }
}
