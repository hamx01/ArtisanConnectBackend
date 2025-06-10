package _11.asktpk.artisanconnectbackend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
public class OrderWithPaymentsDTO {
    private Long orderId;
    private String orderType;
    private String status;
    private Double amount;
    private LocalDateTime createdAt;
    private List<PaymentDTO> payments;
}
