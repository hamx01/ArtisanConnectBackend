package _11.asktpk.artisanconnectbackend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class NoticeAdditionDTO {
    public Long noticeId;
    public String message;

    public NoticeAdditionDTO(String message) {
        this.message = message;
    }

    public NoticeAdditionDTO(Long noticeId, String message) {
        this.noticeId = noticeId;
        this.message = message;
    }
}
