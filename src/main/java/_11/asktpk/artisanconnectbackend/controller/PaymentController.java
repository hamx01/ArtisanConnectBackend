package _11.asktpk.artisanconnectbackend.controller;

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

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    @Value("${tpay.securityCode}")
    private String sellerSecurityCode;

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    @PostMapping(value = "/notification", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<String> handleTpayNotification(@RequestParam Map<String, String> params) {
        log.info("=== ODEBRANO NOTYFIKACJĘ Tpay ===");
        log.info("Parametry:\n{}", paramsToLogString(params));

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
            log.warn("❌ Błędna suma kontrolna! Otrzymano: {}, Oczekiwano: {}", md5sum, expectedMd5);
            return ResponseEntity.status(400).body("INVALID CHECKSUM");
        }

        if ("true".equals(trStatus)) {
            log.info("✅ Transakcja opłacona: tr_id={}, kwota={}", trId, params.get("tr_paid"));
        } else if ("chargeback".equals(trStatus)) {
            log.warn("⚠️ Chargeback: {}", trId);
        }

        return ResponseEntity.ok("TRUE");
    }

    private String paramsToLogString(Map<String, String> params) {
        return params.entrySet().stream()
                .map(e -> e.getKey() + " = " + e.getValue())
                .collect(Collectors.joining("\n"));
    }
}
