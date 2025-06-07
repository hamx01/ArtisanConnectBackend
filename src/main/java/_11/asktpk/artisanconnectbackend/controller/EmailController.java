package _11.asktpk.artisanconnectbackend.controller;
import _11.asktpk.artisanconnectbackend.dto.EmailDTO;
import _11.asktpk.artisanconnectbackend.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/email")
public class EmailController {
    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestBody EmailDTO email) {
        try {
            emailService.sendEmail(email);
            return ResponseEntity.ok("Email wysłany pomyślnie");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Błąd podczas wysyłania emaila");
        }
    }
}