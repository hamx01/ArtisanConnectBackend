package _11.asktpk.artisanconnectbackend.service;

import _11.asktpk.artisanconnectbackend.dto.OAuthPaymentResponseDTO;
import _11.asktpk.artisanconnectbackend.dto.TransactionPaymentRequestDTO;
import _11.asktpk.artisanconnectbackend.dto.TransactionPaymentResponseDTO;
import _11.asktpk.artisanconnectbackend.entities.Order;
import _11.asktpk.artisanconnectbackend.entities.Payment;
import _11.asktpk.artisanconnectbackend.repository.PaymentRepository;
import _11.asktpk.artisanconnectbackend.utils.Enums;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class PaymentService {
    private final WebClient webClient;
    private final String clientId;
    private final String clientSecret;
    private final String authUrl;
    private final String transactionUrl;
    private final PaymentRepository paymentRepository;

    public PaymentService(
            WebClient.Builder webClientBuilder,
            @Value("${tpay.clientId}") String clientId,
            @Value("${tpay.clientSecret}") String clientSecret,
            @Value("${tpay.authUrl}") String authUrl,
            @Value("${tpay.transactionUrl}") String transactionUrl,
            PaymentRepository paymentRepository
    ) {
        this.webClient = webClientBuilder.baseUrl(authUrl).build();
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.authUrl = authUrl;
        this.transactionUrl = transactionUrl;
        this.paymentRepository = paymentRepository;
    }

    public OAuthPaymentResponseDTO getOAuthToken() {
        return webClient.post()
                .uri("")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header("accept", "application/json")
                .body(BodyInserters.fromMultipartData("client_id", clientId)
                        .with("client_secret", clientSecret))
                .retrieve()
                .bodyToMono(OAuthPaymentResponseDTO.class)
                .block();
    }

    public String createTransaction(Order order, String accessToken, TransactionPaymentRequestDTO transactionPaymentRequestDTO) {
        TransactionPaymentResponseDTO response = webClient.post()
                .uri(transactionUrl)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionPaymentRequestDTO)
                .retrieve()
                .bodyToMono(TransactionPaymentResponseDTO.class)
                .block();

        if (response != null && "success".equalsIgnoreCase(response.getResult())) {
            Payment payment = new Payment();
            payment.setOrder(order);
            payment.setAmount(response.getAmount());

            payment.setStatus(Enums.PaymentStatus.PENDING);

            payment.setTransactionId(response.getTransactionId());
            payment.setTransactionPaymentUrl(response.getTransactionPaymentUrl());
            paymentRepository.save(payment);

            System.out.println(response);

            return response.getTransactionPaymentUrl();
        }

        return null;
    }
}
