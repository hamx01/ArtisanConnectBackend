package _11.asktpk.artisanconnectbackend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class TransactionPaymentResponseDTO {
        private String result;
        private String requestId;
        private String transactionId;
        private String title;
        private String posId;
        private String status;
        private DateInfo date;
        private double amount;
        private String currency;
        private String description;
        private String hiddenDescription;
        private Payer payer;
        private Payments payments;
        private String transactionPaymentUrl;

        @Getter
        @Setter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class DateInfo {
            private String creation;
            private String realization;
        }

        @Getter
        @Setter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Payer {
            private String payerId;
            private String email;
            private String name;
            private String phone;
            private String address;
            private String city;
            private String country;
            private String postalCode;
        }

        @Getter
        @Setter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Payments {
            private String status;
            private String method;
            private double amountPaid;
            private DateInfo date;
        }

        @Override
        public String toString() {
            return "YourClassName{" +
                    "result='" + result + '\'' +
                    ", requestId='" + requestId + '\'' +
                    ", transactionId='" + transactionId + '\'' +
                    ", title='" + title + '\'' +
                    ", posId='" + posId + '\'' +
                    ", status='" + status + '\'' +
                    ", date=" + date +
                    ", amount=" + amount +
                    ", currency='" + currency + '\'' +
                    ", description='" + description + '\'' +
                    ", hiddenDescription='" + hiddenDescription + '\'' +
                    ", payer=" + payer +
                    ", payments=" + payments +
                    ", transactionPaymentUrl='" + transactionPaymentUrl + '\'' +
                    '}';
        }

    }


