package _11.asktpk.artisanconnectbackend.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

import _11.asktpk.artisanconnectbackend.utils.Enums.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "notice")
@Getter @Setter
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idNotice;

    private String title;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    private String description;

    private Double price;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime publishDate;

    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL)
    private List<AttributesNotice> attributesNotices;

    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL)
    private List<Order> orders;

//    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL)
//    private List<Payment> payment;
}
