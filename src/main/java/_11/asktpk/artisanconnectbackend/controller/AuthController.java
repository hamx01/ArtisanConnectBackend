package _11.asktpk.artisanconnectbackend.controller;

import _11.asktpk.artisanconnectbackend.customExceptions.ClientAlreadyExistsException;
import _11.asktpk.artisanconnectbackend.customExceptions.WrongLoginPasswordException;
import _11.asktpk.artisanconnectbackend.dto.*;
import _11.asktpk.artisanconnectbackend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequestDTO authRequestDTO) {
        if (authRequestDTO.getEmail() == null || authRequestDTO.getPassword() == null
                || authRequestDTO.getEmail().isEmpty() || authRequestDTO.getPassword().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponseDTO("Przekazano puste login lub hasło"));
        }

        authRequestDTO.setEmail(authRequestDTO.getEmail().toLowerCase());

        try {
            AuthResponseDTO responseDTO = authService.login(authRequestDTO.getEmail(), authRequestDTO.getPassword());

            return ResponseEntity.status(HttpStatus.OK)
                    .body(responseDTO);

        } catch (WrongLoginPasswordException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new RequestResponseDTO(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponseDTO(e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody ClientRegistrationDTO clientRegistrationDTO) {
        if (clientRegistrationDTO.getEmail() == null || clientRegistrationDTO.getPassword() == null
                || clientRegistrationDTO.getEmail().isEmpty() || clientRegistrationDTO.getPassword().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponseDTO("Przekazano puste login lub hasło"));
        }

        clientRegistrationDTO.setEmail(clientRegistrationDTO.getEmail().toLowerCase());

        try {
            AuthResponseDTO registrationData = authService.register(clientRegistrationDTO.getEmail(), clientRegistrationDTO.getPassword(), clientRegistrationDTO.getFirstName(), clientRegistrationDTO.getLastName());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(registrationData);
        } catch (ClientAlreadyExistsException clientAlreadyExistsException) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new RequestResponseDTO(clientAlreadyExistsException.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponseDTO(e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<RequestResponseDTO> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            authService.logout(token);
            return ResponseEntity.ok(new RequestResponseDTO("Successfully logged out"));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponseDTO("Invalid token"));
    }

    @PostMapping("/google")
    public ResponseEntity<?> authenticateWithGoogle(@RequestBody GoogleAuthRequestDTO dto) {
        if(dto.getGoogleToken() == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponseDTO("Invalid or empty token"));
        }

        try {
            AuthResponseDTO response = authService.googleLogin(dto.getGoogleToken());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (HttpClientErrorException httpClientErrorException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponseDTO("Google access token is invalid or expired"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new RequestResponseDTO("Authentication Error (Google): " + e.getMessage()));
        }
    }
}