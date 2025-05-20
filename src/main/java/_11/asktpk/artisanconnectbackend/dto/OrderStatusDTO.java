package _11.asktpk.artisanconnectbackend.dto;


import _11.asktpk.artisanconnectbackend.utils.Enums;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrderStatusDTO {
    public long id;
    public Enums.OrderStatus status;
}
