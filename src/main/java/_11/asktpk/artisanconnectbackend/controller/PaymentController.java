package _11.asktpk.artisanconnectbackend.controller;

import _11.asktpk.artisanconnectbackend.entities.Notice;
import _11.asktpk.artisanconnectbackend.entities.Order;
import _11.asktpk.artisanconnectbackend.entities.Payment;
import _11.asktpk.artisanconnectbackend.repository.PaymentRepository;
import _11.asktpk.artisanconnectbackend.utils.Enums;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    @Value("${tpay.securityCode}")
    private String sellerSecurityCode;

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentRepository paymentRepository;

    public PaymentController(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @PostMapping(value = "/notification", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<String> handleTpayNotification(@RequestParam Map<String, String> params) {
        String id = params.get("id");
        String trId = params.get("tr_id");
        String trAmount = params.get("tr_amount");
        String trCrc = params.get("tr_crc");
        String md5sum = params.get("md5sum");
        String trStatus = params.get("tr_status");

        String expectedMd5 = DigestUtils.md5DigestAsHex(
                (id + trId + trAmount + trCrc + sellerSecurityCode).getBytes()
        );

        if (!expectedMd5.equals(md5sum)) {
            return ResponseEntity.status(400).body("INVALID CHECKSUM");
        }

        Optional<Payment> optionalPayment = paymentRepository.findByTransactionId(trId);
        if (optionalPayment.isPresent()) {
            Payment payment = optionalPayment.get();

            if ("true".equalsIgnoreCase(trStatus) || "PAID".equalsIgnoreCase(trStatus)) {
                payment.setStatus(Enums.PaymentStatus.CORRECT);

                if (payment.getOrder() != null) {
                    Order order = payment.getOrder();
                    order.setStatus(Enums.OrderStatus.COMPLETED);
                    Notice notice = order.getNotice();
                    if (order.getOrderType() == Enums.OrderType.ACTIVATION) {
                        notice.setStatus(Enums.Status.ACTIVE);
                    } else if (order.getOrderType() == Enums.OrderType.BOOST) {
                        notice.setPublishDate(LocalDateTime.now());
                    }
                }

            } else if ("false".equalsIgnoreCase(trStatus)) {
                payment.setStatus(Enums.PaymentStatus.INCORRECT);

                if (payment.getOrder() != null) {
                    payment.getOrder().setStatus(Enums.OrderStatus.CANCELLED);
                }
            }

            paymentRepository.save(payment);
        }
        return ResponseEntity.ok("TRUE");
    }

    private String paramsToLogString(Map<String, String> params) {
        return params.entrySet().stream()
                .map(e -> e.getKey() + " = " + e.getValue())
                .collect(Collectors.joining("\n"));
    }
}
