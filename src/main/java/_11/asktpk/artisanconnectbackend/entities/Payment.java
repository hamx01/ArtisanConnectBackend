package _11.asktpk.artisanconnectbackend.entities;

import _11.asktpk.artisanconnectbackend.utils.Enums;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "payment")
@Getter @Setter
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPayment;

    @ManyToOne
    @JoinColumn(name = "id_order")
    private Order order;

    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Enums.PaymentStatus status;

    private String transactionPaymentUrl;

    private String transactionId;
}
