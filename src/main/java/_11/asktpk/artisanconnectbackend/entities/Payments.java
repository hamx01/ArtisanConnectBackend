package _11.asktpk.artisanconnectbackend.entities;

import _11.asktpk.artisanconnectbackend.utils.Enums.Status;

import jakarta.persistence.*;

@Entity
@Table(name = "payments")
public class Payments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPayment;

    @ManyToOne
    @JoinColumn(name = "id_order")
    private Orders order;

    @ManyToOne
    @JoinColumn(name = "id_notice")
    private Notice notice;

    private Double noticePublishPrice;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String sessionId;

    // Getters, setters, and constructors
}
