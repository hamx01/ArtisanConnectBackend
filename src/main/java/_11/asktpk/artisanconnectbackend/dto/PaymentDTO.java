package _11.asktpk.artisanconnectbackend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentDTO {
    private Long paymentId;
    private Double amount;
    private String status;
    private String transactionPaymentUrl;
    private String transactionId;

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTransactionPaymentUrl(String transactionPaymentUrl) {
        this.transactionPaymentUrl = transactionPaymentUrl;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
