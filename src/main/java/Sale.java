import java.time.Instant;

public class Sale {
    private Integer id;
    private Order order;
    private Instant creationDatetime;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public Instant getCreationDatetime() { return creationDatetime; }
    public void setCreationDatetime(Instant creationDatetime) { this.creationDatetime = creationDatetime; }

    @Override
    public String toString() {
        return "Sale{" +
                "id=" + id +
                ", orderRef=" + (order != null ? order.getReference() : "null") +
                ", created=" + creationDatetime +
                '}';
    }
}
