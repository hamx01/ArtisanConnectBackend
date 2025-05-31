package _11.asktpk.artisanconnectbackend.controller;

import _11.asktpk.artisanconnectbackend.dto.*;
import _11.asktpk.artisanconnectbackend.entities.Client;
import _11.asktpk.artisanconnectbackend.security.JwtUtil;
import _11.asktpk.artisanconnectbackend.service.ClientService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final ClientService clientService;
    private final JwtUtil jwtUtil;

    public AuthController(ClientService clientService, JwtUtil jwtUtil) {
        this.clientService = clientService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO authRequestDTO) {
        if (clientService.checkClientCredentials(authRequestDTO)) {
            Client client = clientService.getClientByEmail(authRequestDTO.getEmail());
            Long userId = client.getId();
            String userRole = client.getRole().getRole();

            String token = jwtUtil.generateToken(client.getEmail(), userRole, userId);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new AuthResponseDTO(userId, userRole, token));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody ClientRegistrationDTO clientDTO) {
        if (clientService.getClientByEmail(clientDTO.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        ClientDTO savedClient = clientService.registerClient(clientDTO);

        String token = jwtUtil.generateToken(
                savedClient.getEmail(),
                savedClient.getRole().getRole(),
                savedClient.getId()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponseDTO(
                        savedClient.getId(),
                        savedClient.getRole().getRole(),
                        token
                ));
    }

    @PostMapping("/logout")
    public ResponseEntity<RequestResponseDTO> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            jwtUtil.blacklistToken(token);
            return ResponseEntity.ok(new RequestResponseDTO("Successfully logged out"));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponseDTO("Invalid token"));
    }
}