package _11.asktpk.artisanconnectbackend.service;

import _11.asktpk.artisanconnectbackend.customExceptions.ClientAlreadyExistsException;
import _11.asktpk.artisanconnectbackend.customExceptions.WrongLoginPasswordException;
import _11.asktpk.artisanconnectbackend.dto.AuthResponseDTO;
import _11.asktpk.artisanconnectbackend.dto.ClientDTO;
import _11.asktpk.artisanconnectbackend.entities.Client;
import _11.asktpk.artisanconnectbackend.security.JwtUtil;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

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

        if (passwordEncoder.matches(password, client.getPassword())) {
            String token = jwtUtil.generateToken(client.getEmail(), client.getRole().getRole(), client.getId());
            log.info("User logged in with {}", client.getEmail());
            return new AuthResponseDTO(client.getId(), client.getRole().getRole(), token);
        }
        throw new WrongLoginPasswordException("Login lub hasło jest niepoprawny!");
    }

    public AuthResponseDTO register(String email, String password, String firstName, String lastName) throws Exception {
        if (clientService.getClientByEmail(email) != null) {
            throw new ClientAlreadyExistsException("Klient o podanym adresie email już istnieje!");
        }

        Client newClient = new Client();
        newClient.setEmail(email);
        newClient.setPassword(passwordEncoder.encode(password));
        newClient.setFirstName(firstName);
        newClient.setLastName(lastName);

        ClientDTO savedClient = clientService.registerClient(newClient);
        if (savedClient != null) {
            log.info("New user registered with {}", savedClient.getEmail());
            String token = jwtUtil.generateToken(
                    savedClient.getEmail(),
                    savedClient.getRole(),
                    savedClient.getId()
            );

            return new AuthResponseDTO(savedClient.getId(), savedClient.getRole(), token);
        } else {
            throw new Exception("Rejestracja nie powiodła się!");
        }
    }

    public void logout(String token) {
        jwtUtil.blacklistToken(token);
    }

    public AuthResponseDTO googleLogin(String googleAccessToken) throws Exception {
        String googleUserInfoUrl = "https://www.googleapis.com/oauth2/v3/userinfo";

        ResponseEntity<Map> response;


        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(googleAccessToken);
        RestTemplate restTemplate = new RestTemplate();
        response = restTemplate.exchange(
                googleUserInfoUrl, HttpMethod.GET, new HttpEntity<>(headers), Map.class);


        Map<String, Object> userInfo = response.getBody();

//                String googleId = (String) userInfo.get("sub");  Potencjalnie możemy używać googlowskiego ID, ale to ma konflikt z naszym generowanym
        if (userInfo == null) {
            throw new Exception("Pobrany użytkownik jest pusty! Może to być spowodowane niepoprawnym tokenem lub brakiem dostępu do Google API.");
        }
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
        log.info("User authenticated with google: {}", client.getEmail());
        return new AuthResponseDTO(
                client.getId(),
                client.getRole().getRole(),
                jwt
        );
    }

}

