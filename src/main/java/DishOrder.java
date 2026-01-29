import java.math.BigDecimal;
import java.util.Objects;

public class DishOrder {
    private Integer id;
    private Order order;
    private Dish dish;
    private Integer quantity;

    public DishOrder() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public Dish getDish() { return dish; }
    public void setDish(Dish dish) { this.dish = dish; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getSubTotal() {
        if (dish == null || dish.getSellingPrice() == null || quantity == null) {
            return BigDecimal.ZERO;
        }
        return dish.getSellingPrice().multiply(BigDecimal.valueOf(quantity));
    }

    @Override
    public String toString() {
        return "DishOrder{" +
                "dish=" + (dish != null ? dish.getName() : "null") +
                ", quantity=" + quantity +
                '}';
    }
}
