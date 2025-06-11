package _11.asktpk.artisanconnectbackend;

import _11.asktpk.artisanconnectbackend.customExceptions.ClientAlreadyExistsException;
import _11.asktpk.artisanconnectbackend.customExceptions.WrongLoginPasswordException;
import _11.asktpk.artisanconnectbackend.dto.AuthResponseDTO;
import _11.asktpk.artisanconnectbackend.dto.ClientDTO;
import _11.asktpk.artisanconnectbackend.entities.Client;
import _11.asktpk.artisanconnectbackend.entities.Role;
import _11.asktpk.artisanconnectbackend.security.JwtUtil;
import _11.asktpk.artisanconnectbackend.service.AuthService;
import _11.asktpk.artisanconnectbackend.service.ClientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    private final ClientService clientService = Mockito.mock(ClientService.class);
    private final PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
    private final JwtUtil jwtUtil = Mockito.mock(JwtUtil.class);
    private final AuthService authService = new AuthService(clientService, jwtUtil, passwordEncoder);


    @Test
    @DisplayName("Test logowania - poprawne dane")
    public void testLoginSuccess() throws Exception {
        String email = "test@example.com";
        String password = "password";
        Client client = new Client();
        client.setEmail(email);
        client.setPassword("encodedPassword");
        client.setRole(new Role());

        when(clientService.getClientByEmail(email)).thenReturn(client);
        when(passwordEncoder.matches(password, client.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(email, client.getRole().getRole(), client.getId())).thenReturn("token");

        AuthResponseDTO response = authService.login(email, password);

        assertNotNull(response, "Odpowiedź nie powinna być null");
        assertEquals("token", response.getToken(), "Token w odpowiedzi powinien być poprawny");
    }

    @Test
    @DisplayName("Test logowania - niepoprawne hasło")
    public void testLoginWrongPassword() {
        String email = "test@example.com";
        String password = "wrongPassword";
        Client client = new Client();
        client.setEmail(email);
        client.setPassword("encodedPassword");

        when(clientService.getClientByEmail(email)).thenReturn(client);
        when(passwordEncoder.matches(password, client.getPassword())).thenReturn(false);

        assertThrows(WrongLoginPasswordException.class, () -> authService.login(email, password),
                "Powinien zostać rzucony WrongLoginPasswordException");
    }

    @Test
    @DisplayName("Test rejestracji - nowy użytkownik")
    public void testRegisterNewUser() throws Exception {
        String email = "new@example.com";
        String password = "password";
        String firstName = "Jan";
        String lastName = "Kowalski";

        when(clientService.getClientByEmail(email)).thenReturn(null);
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");
        when(clientService.registerClient(any(Client.class))).thenReturn(new ClientDTO());

        AuthResponseDTO response = authService.register(email, password, firstName, lastName);

        assertNotNull(response, "Odpowiedź nie powinna być null");
    }

    @Test
    @DisplayName("Test rejestracji - użytkownik już istnieje")
    public void testRegisterExistingUser() {
        String email = "existing@example.com";
        String password = "password";
        String firstName = "Jan";
        String lastName = "Kowalski";

        when(clientService.getClientByEmail(email)).thenReturn(new Client());

        assertThrows(ClientAlreadyExistsException.class, () -> authService.register(email, password, firstName, lastName),
                "Powinien zostać rzucony ClientAlreadyExistsException");
    }

    @Test
    @DisplayName("Test wylogowania z poprawnym tokenem")
    public void testLogoutWithValidToken() {
        String token = "valid.token.here";

        when(jwtUtil.isBlacklisted(token)).thenReturn(false);

        authService.logout(token);

        verify(jwtUtil, times(1)).blacklistToken(token);

        when(jwtUtil.isBlacklisted(token)).thenReturn(true);
        assertTrue(jwtUtil.isBlacklisted(token), "Token powinien być na czarnej liście po wylogowaniu");
    }

    @Test
    @DisplayName("Test wylogowania bez tokena")
    public void testLogoutWithoutToken() {
        authService.logout(null);

        verify(jwtUtil, never()).blacklistToken(anyString());

    }


}