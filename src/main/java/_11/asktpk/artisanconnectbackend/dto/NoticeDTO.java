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

    private Enums.Status status;

    private LocalDateTime publishDate;

    private List<AttributesNotice> attributesNotices;

    public NoticeDTO() {

    }
}
