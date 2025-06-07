package _11.asktpk.artisanconnectbackend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
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
