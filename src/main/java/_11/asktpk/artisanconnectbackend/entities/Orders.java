package _11.asktpk.artisanconnectbackend.entities;

import _11.asktpk.artisanconnectbackend.utils.Enums.Status;

import jakarta.persistence.*;

@Entity
@Table(name = "orders")
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idOrder;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "id_notice")
    private Notice notice;

    @Enumerated(EnumType.STRING)
    private Status status;

    // Getters, setters, and constructors
}
