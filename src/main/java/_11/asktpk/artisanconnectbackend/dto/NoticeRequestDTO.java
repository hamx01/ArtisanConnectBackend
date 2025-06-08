package _11.asktpk.artisanconnectbackend.dto;

import _11.asktpk.artisanconnectbackend.utils.Enums;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class NoticeRequestDTO {
    private String title;

    private Long clientId;

    private String description;

    private Double price;

    private Enums.Category category;

    private Enums.Status status;

    public NoticeRequestDTO() {

    }
}
