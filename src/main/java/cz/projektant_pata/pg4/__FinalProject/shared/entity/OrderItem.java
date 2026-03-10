package cz.projektant_pata.pg4.__FinalProject.shared.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "order_items")
@Data
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String food;

    private Integer count;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private BigDecimal price;

    @JsonManagedReference
    @OneToMany(mappedBy = "orderItem", cascade = CascadeType.ALL)
    private List<OrderItemChangeable> orderItemChangeables;
}
