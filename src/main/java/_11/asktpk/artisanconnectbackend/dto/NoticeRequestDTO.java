package _11.asktpk.artisanconnectbackend.dto;

import _11.asktpk.artisanconnectbackend.utils.Enums;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter @Setter
public class NoticeRequestDTO {
    private String title;

    private Long clientId;

    private String description;

    private Double price;

    private Enums.Category category;

    private Enums.Status status;

    private List<AttributeDto> attributes;

    public NoticeRequestDTO() {

    }
}
