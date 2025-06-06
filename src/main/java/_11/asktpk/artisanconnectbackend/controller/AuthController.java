package _11.asktpk.artisanconnectbackend.controller;

import _11.asktpk.artisanconnectbackend.dto.*;
import _11.asktpk.artisanconnectbackend.entities.Client;
import _11.asktpk.artisanconnectbackend.security.JwtUtil;
import _11.asktpk.artisanconnectbackend.service.ClientService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
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

            log.info("User logged in with {}", client.getEmail());
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
                savedClient.getRole(),
                savedClient.getId()
        );

        log.info("New user registered with {}", savedClient.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponseDTO(
                        savedClient.getId(),
                        savedClient.getRole(),
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

    @PostMapping("/google")
    public ResponseEntity<?> authenticateWithGoogle(@RequestBody GoogleAuthRequestDTO dto) {
        try {
            String accessToken = dto.getGoogleToken();
            String googleUserInfoUrl = "https://www.googleapis.com/oauth2/v3/userinfo";

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> response = restTemplate.exchange(
                    googleUserInfoUrl, HttpMethod.GET, entity, Map.class);

            Map<String, Object> userInfo = response.getBody();

//                String googleId = (String) userInfo.get("sub");  Potencjalnie możemy używać googlowskiego ID, ale to ma konflikt z naszym generowanym
            assert userInfo != null;
            String email = (String) userInfo.get("email");
            String name = (String) userInfo.get("name");

            Client client = clientService.getClientByEmail(email);
            if (client == null) {
                client = new Client();
                client.setEmail(email);
                client.setFirstName(name);
                client.setRole(clientService.getUserRole()); // to pobiera po prostu role "USER" z tabeli w bazie
                clientService.saveClientToDB(client);
            }

            String jwt = jwtUtil.generateToken(client.getEmail(), client.getRole().getRole(), client.getId());

            log.info("User authenticated with google: {}", email);
            return ResponseEntity.ok(new AuthResponseDTO(client.getId(), client.getRole().getRole(), jwt));
        } catch (HttpClientErrorException httpClientErrorException) {
            log.error("Token is invalid or expired");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new RequestResponseDTO("Invalid access token"));
        } catch (Exception e) {
            log.error("Error while checking Google access token", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new RequestResponseDTO("Authentication Error (Google): " + e.getMessage()));
        }
    }
}