public class Dish {
    private int id;
    private String name;
    private String dishType;
    private double price;

    public Dish() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDishType() { return dishType; }
    public void setDishType(String dishType) { this.dishType = dishType; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getDishCost() {
        DataRetriever retriever = new DataRetriever();
        Dish fullDish = retriever.findDishById(this.id);
        if (fullDish != null) {
            return fullDish.getPrice();
        }
        return 0.0;
    }

    @Override
    public String toString() {
        return "Dish{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", dishType='" + dishType + '\'' +
                ", price=" + price +
                '}';
    }
}