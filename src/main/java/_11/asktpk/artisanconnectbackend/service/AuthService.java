package _11.asktpk.artisanconnectbackend.service;

import _11.asktpk.artisanconnectbackend.dto.AuthResponseDTO;
import _11.asktpk.artisanconnectbackend.entities.Client;
import _11.asktpk.artisanconnectbackend.security.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthService {

    private final ClientService clientService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(ClientService clientService, JwtUtil jwtUtil) {
        this.clientService = clientService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public AuthResponseDTO login(String email, String password) throws Exception {
        Client client = clientService.getClientByEmail(email);
        if (client == null) {
            throw new Exception("Klient o podanym adresie nie istnieje!");
        }

        if(passwordEncoder.matches(password, client.getPassword())) {
            String token = jwtUtil.generateToken(client.getEmail(), client.getRole().getRole(), client.getId());
            log.info("User logged in with {}", client.getEmail());
            return new AuthResponseDTO(client.getId(), client.getRole().getRole(), token);
        }
        throw new Exception("Login lub hasło jest niepoprawny!");
    }
}

