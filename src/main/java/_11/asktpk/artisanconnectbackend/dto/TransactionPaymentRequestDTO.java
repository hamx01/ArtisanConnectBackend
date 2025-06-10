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
    private Callbacks callbacks;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Payer {
        private String email;
        private String name;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Callbacks {
        private PayerUrls payerUrls;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PayerUrls {
        private String success;
        private String error;
    }
}
