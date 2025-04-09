package _11.asktpk.artisanconnectbackend.DTO;

import _11.asktpk.artisanconnectbackend.Entities.AttributesNotice;
import _11.asktpk.artisanconnectbackend.Entities.Orders;
import _11.asktpk.artisanconnectbackend.Entities.Payments;
import _11.asktpk.artisanconnectbackend.Utils.Enums;

import java.time.LocalDate;
import java.util.List;

public class NoticeDTO {
    private String title;
    private Long clientId;
    private String description;
    private Double price;
    private Enums.Category category;
    private List<String> images;
    private Enums.Status status;
    private LocalDate publishDate;
    private List<AttributesNotice> attributesNotices;
    private List<Orders> orders;
    private List<Payments> payments;

    public NoticeDTO(String title, Long clientId, String description, Double price,
                     Enums.Category category, List<String> images, Enums.Status status,
                     LocalDate publishDate, List<AttributesNotice> attributesNotices,
                     List<Orders> orders, List<Payments> payments) {
        this.title = title;
        this.clientId = clientId;
        this.description = description;
        this.price = price;
        this.category = category;
        this.images = images;
        this.status = status;
        this.publishDate = publishDate;
        this.attributesNotices = attributesNotices;
        this.orders = orders;
        this.payments = payments;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Enums.Category getCategory() {
        return category;
    }

    public void setCategory(Enums.Category category) {
        this.category = category;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public Enums.Status getStatus() {
        return status;
    }

    public void setStatus(Enums.Status status) {
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

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
}
