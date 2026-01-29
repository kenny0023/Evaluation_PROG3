import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private Integer id;
    private String reference;
    private Instant creationDatetime;
    private BigDecimal totalTtc;
    private List<DishOrder> dishOrders = new ArrayList<>();

    public Order() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public Instant getCreationDatetime() { return creationDatetime; }
    public void setCreationDatetime(Instant creationDatetime) { this.creationDatetime = creationDatetime; }

    public BigDecimal getTotalTtc() { return totalTtc; }
    public void setTotalTtc(BigDecimal totalTtc) { this.totalTtc = totalTtc; }

    public List<DishOrder> getDishOrders() { return new ArrayList<>(dishOrders); }

    public void addDishOrder(DishOrder dishOrder) {
        if (dishOrder != null) {
            dishOrder.setOrder(this);
            dishOrders.add(dishOrder);
        }
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", reference='" + reference + '\'' +
                ", creationDatetime=" + creationDatetime +
                ", totalTtc=" + totalTtc +
                ", dishOrdersCount=" + dishOrders.size() +
                '}';
    }
}
