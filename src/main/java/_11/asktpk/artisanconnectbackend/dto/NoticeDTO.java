package _11.asktpk.artisanconnectbackend.dto;

import _11.asktpk.artisanconnectbackend.entities.AttributesNotice;
import _11.asktpk.artisanconnectbackend.utils.Enums;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
public class NoticeDTO {
    private long noticeId;

    private String title;

    private Long clientId;

    private String description;

    private Double price;

    private Enums.Category category;

    private List<String> images;

    private Enums.Status status;

    private LocalDateTime publishDate;

    private List<AttributesNotice> attributesNotices;

    public NoticeDTO() {

    }

    public NoticeDTO(Long noticeId, String title, Long clientId, String description, Double price,
                     Enums.Category category, List<String> images, Enums.Status status,
                     LocalDateTime publishDate, List<AttributesNotice> attributesNotices) {
        this.noticeId = noticeId;
        this.title = title;
        this.clientId = clientId;
        this.description = description;
        this.price = price;
        this.category = category;
        this.images = images;
        this.status = status;
        this.publishDate = publishDate;
        this.attributesNotices = attributesNotices;
    }
}
