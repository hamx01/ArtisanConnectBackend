package _11.asktpk.artisanconnectbackend.entities;

import _11.asktpk.artisanconnectbackend.utils.Enums;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_client")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "id_notice")
    private Notice notice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Enums.OrderType orderType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Enums.OrderStatus status;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
