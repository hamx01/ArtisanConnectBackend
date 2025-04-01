package _11.asktpk.artisanconnectbackend.Model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

import _11.asktpk.artisanconnectbackend.Utils.Enums.*;

@Entity
@Table(name = "notice")
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

    @ElementCollection
    private List<String> images;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDate publishDate;

    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL)
    private List<AttributesNotice> attributesNotices;

    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL)
    private List<Orders> orders;

    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL)
    private List<Payments> payments;

    // Getters, setters, and constructors
}
