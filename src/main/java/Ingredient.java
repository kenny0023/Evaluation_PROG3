//import java.math.BigDecimal;
//
//public class Ingredient {
//    private Integer id;
//    private String name;
//    private BigDecimal price;
//    private CategoryEnum category;
//
//    public Ingredient() {}
//
//    public Ingredient(Integer id, String name, BigDecimal price, CategoryEnum category) {
//        this.id = id;
//        this.name = name;
//        this.price = price;
//        this.category = category;
//    }
//
//    // Getters & Setters
//    public Integer getId() { return id; }
//    public void setId(Integer id) { this.id = id; }
//    public String getName() { return name; }
//    public void setName(String name) { this.name = name; }
//    public BigDecimal getPrice() { return price; }
//    public void setPrice(BigDecimal price) { this.price = price; }
//    public CategoryEnum getCategory() { return category; }
//    public void setCategory(CategoryEnum category) { this.category = category; }
//
//    @Override
//    public String toString() {
//        return String.format("%s (%.0f Ar/%s)", name, price, category);
//    }
//}

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Classe Ingredient complète pour TD4 - Gestion de stocks
 */
public class Ingredient {

    private Integer id;
    private String name;
    private BigDecimal price;
    private CategoryEnum category;

    // Liste des mouvements de stock (IN / OUT)
    private List<StockMovement> stockMovements = new ArrayList<>();

    // Constructeurs
    public Ingredient(int id, String laitue, double v, CategoryEnum vegetable) {
    }

    public Ingredient(Integer id, String name, BigDecimal price, CategoryEnum category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public CategoryEnum getCategory() {
        return category;
    }

    public void setCategory(CategoryEnum category) {
        this.category = category;
    }

    public List<StockMovement> getStockMovements() {
        return new ArrayList<>(stockMovements); // copie défensive
    }

    public void setStockMovements(List<StockMovement> stockMovements) {
        this.stockMovements = new ArrayList<>(stockMovements);
    }

    // Méthode pour ajouter un mouvement (pratique)
    public void addStockMovement(StockMovement movement) {
        if (movement != null) {
            // On associe automatiquement l'ingrédient au mouvement
            movement.setIngredientId(this.id);
            stockMovements.add(movement);
        }
    }

    public StockValue getStockValueAt(Instant t) {
        if (t == null) {
            throw new IllegalArgumentException("Instant t ne peut pas être null");
        }

        double total = 0.0;
        UnitTypeEnum referenceUnit = null;

        for (StockMovement m : stockMovements) {
            // On ne considère que les mouvements <= t
            if (m.getCreationDatetime() != null && !m.getCreationDatetime().isAfter(t)) {
                // Vérification unité homogène
                if (referenceUnit == null) {
                    referenceUnit = m.getUnit();
                } else if (referenceUnit != m.getUnit()) {
                    throw new IllegalStateException("Unités différentes détectées pour l'ingrédient " + name);
                }

                // Ajout ou soustraction
                if (m.getType() == MovementTypeEnum.IN) {
                    total += m.getQuantity();
                } else if (m.getType() == MovementTypeEnum.OUT) {
                    total -= m.getQuantity();
                }
            }
        }

        // Si aucun mouvement → stock = 0
        if (referenceUnit == null) {
            referenceUnit = UnitTypeEnum.KG; // unité par défaut raisonnable
        }

        return new StockValue(total, referenceUnit);
    }

    // toString pour debug facile
    @Override
    public String toString() {
        return "Ingredient{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", category=" + category +
                ", stockMovementsCount=" + stockMovements.size() +
                '}';
    }

    // equals & hashCode (utile pour les tests)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ingredient that = (Ingredient) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(price, that.price) &&
                category == that.category;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price, category);
    }
}