import java.math.BigDecimal;
import java.time.Instant;

public class Main {
    public static void main(String[] args) {
        try (DishDataRetriever dr = new DishDataRetriever()) {

            String ref = "ORD-TEST-" + System.currentTimeMillis();

            Order commande = new Order();
            commande.setReference(ref);
            commande.setCreationDatetime(Instant.now());
            commande.setTotalTtc(new BigDecimal("3500.00"));
            commande.setPaymentStatus(PaymentStatusEnum.UNPAID);

            Dish salade = dr.findDishById(1);
            if (salade != null) {
                DishOrder ligne = new DishOrder();
                ligne.setDish(salade);
                ligne.setQuantity(1);
                commande.addDishOrder(ligne);
            }

            Order saved = dr.saveOrder(commande);
            System.out.println("Commande créée : " + saved.getReference());

            dr.markOrderAsPaid(ref);
            System.out.println("Commande payée avec succès");

            Order paid = dr.findOrderByReference(ref);
            System.out.println("Statut après paiement : " + paid.getPaymentStatus());

            Sale sale = dr.createSaleFrom(paid);
            System.out.println("Vente créée : " + sale);

        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
