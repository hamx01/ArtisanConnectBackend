package _11.asktpk.artisanconnectbackend.Entities;

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

    public Long getIdNotice() {
        return idNotice;
    }

    public void setIdNotice(Long idNotice) {
        this.idNotice = idNotice;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDate getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(LocalDate publishDate) {
        this.publishDate = publishDate;
    }

    public List<AttributesNotice> getAttributesNotices() {
        return attributesNotices;
    }

    public void setAttributesNotices(List<AttributesNotice> attributesNotices) {
        this.attributesNotices = attributesNotices;
    }

    public List<Orders> getOrders() {
        return orders;
    }

    public void setOrders(List<Orders> orders) {
        this.orders = orders;
    }

    public List<Payments> getPayments() {
        return payments;
    }

    public void setPayments(List<Payments> payments) {
        this.payments = payments;
    }

    // Getters, setters, and constructors
}
