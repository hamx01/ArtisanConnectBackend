package _11.asktpk.artisanconnectbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionPaymentRequestDTO {
    private double amount;
    private String description;
    private Payer payer;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Payer {
        private String email;
        private String name;
    }
}
