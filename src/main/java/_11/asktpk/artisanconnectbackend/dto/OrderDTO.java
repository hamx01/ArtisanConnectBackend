package _11.asktpk.artisanconnectbackend.dto;

import _11.asktpk.artisanconnectbackend.utils.Enums;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDTO {
    private Long clientId;
    private Long noticeId;
    private Enums.OrderType orderType;
}
