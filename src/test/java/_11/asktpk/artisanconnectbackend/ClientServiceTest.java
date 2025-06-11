package _11.asktpk.artisanconnectbackend;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import _11.asktpk.artisanconnectbackend.dto.ClientDTO;
import _11.asktpk.artisanconnectbackend.entities.Client;
import _11.asktpk.artisanconnectbackend.entities.Role;
import _11.asktpk.artisanconnectbackend.repository.ClientRepository;
import _11.asktpk.artisanconnectbackend.repository.RolesRepository;
import _11.asktpk.artisanconnectbackend.service.ClientService;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ClientServiceTest {

    private final ClientRepository clientRepository = Mockito.mock(ClientRepository.class);
    private final RolesRepository rolesRepository = Mockito.mock(RolesRepository.class);

    private final ClientService clientService = new ClientService(clientRepository, rolesRepository);

    @Test
    @DisplayName("Test pobierania wszystkich klientów")
    public void testGetAllClients() {
        Client client1 = new Client();
        client1.setId(1L);
        client1.setEmail("client1@example.com");
        client1.setRole(new Role());

        Client client2 = new Client();
        client2.setId(2L);
        client2.setEmail("client2@example.com");
        client2.setRole(new Role());

        when(clientRepository.findAll()).thenReturn(List.of(client1, client2));

        List<ClientDTO> clients = clientService.getAllClients();

        assertEquals(2, clients.size(), "Lista klientów powinna zawierać 2 elementy");
        assertEquals("client1@example.com", clients.getFirst().getEmail(), "Email pierwszego klienta powinien być poprawny");

        System.out.println("Test pobierania wszystkich klientów przeszedł pomyślnie.");
    }

    @Test
    @DisplayName("Test pobierania klienta po ID - klient istnieje")
    public void testGetClientByIdExists() {
        // Przygotowanie danych
        Long clientId = 1L;
        Client client = new Client();
        client.setId(clientId);
        client.setEmail("client@example.com");
        client.setRole(new Role());

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));

        Client retrievedClient = clientService.getClientById(clientId);

        assertNotNull(retrievedClient, "Pobrany klient nie powinien być null");
        assertEquals(clientId, retrievedClient.getId(), "ID klienta powinno być zgodne");

        System.out.println("Test pobierania klienta po ID (klient istnieje) przeszedł pomyślnie.");
    }

    @Test
    @DisplayName("Test pobierania klienta po ID - klient nie istnieje")
    public void testGetClientByIdNotExists() {
        Long clientId = 1L;
        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        Client retrievedClient = clientService.getClientById(clientId);

        assertNull(retrievedClient, "Pobrany klient powinien być null, gdy nie istnieje");

        System.out.println("Test pobierania klienta po ID (klient nie istnieje) przeszedł pomyślnie.");
    }

    @Test
    @DisplayName("Test pobierania klienta po emailu")
    public void testGetClientByEmail() {
        String email = "client@example.com";
        Client client = new Client();
        client.setEmail(email);

        when(clientRepository.findByEmail(email)).thenReturn(client);

        Client retrievedClient = clientService.getClientByEmail(email);

        assertNotNull(retrievedClient, "Pobrany klient nie powinien być null");
        assertEquals(email, retrievedClient.getEmail(), "Email klienta powinien być zgodny");

        System.out.println("Test pobierania klienta po emailu przeszedł pomyślnie.");
    }

    @Test
    @DisplayName("Test dodawania klienta")
    public void testAddClient() {
        // Przygotowanie danych
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setEmail("newclient@example.com");
        clientDTO.setRole("USER");

        Client client = new Client();
        client.setEmail("newclient@example.com");
        client.setRole(new Role());

        when(clientRepository.save(any(Client.class))).thenReturn(client);

        ClientDTO addedClient = clientService.addClient(clientDTO);

        assertNotNull(addedClient, "Dodany klient nie powinien być null");
        assertEquals("newclient@example.com", addedClient.getEmail(), "Email dodanego klienta powinien być poprawny");

        System.out.println("Test dodawania klienta przeszedł pomyślnie.");
    }

    @Test
    @DisplayName("Test aktualizacji klienta")
    public void testUpdateClient() {
        Long clientId = 1L;
        Client existingClient = new Client();
        existingClient.setId(clientId);
        existingClient.setEmail("old@example.com");

        ClientDTO updatedDTO = new ClientDTO();
        updatedDTO.setEmail("updated@example.com");
        updatedDTO.setRole("USER");

        Role role = new Role();
        role.setRole("USER");

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(existingClient));
        when(rolesRepository.findRoleByRole("USER")).thenReturn(role);
        when(clientRepository.save(any(Client.class))).thenReturn(existingClient);

        ClientDTO updatedClient = clientService.updateClient(clientId, updatedDTO);

        assertEquals("updated@example.com", updatedClient.getEmail(), "Email klienta powinien być zaktualizowany");

        System.out.println("Test aktualizacji klienta przeszedł pomyślnie.");
    }

    @Test
    @DisplayName("Test aktualizacji klienta - klient nie istnieje")
    public void testUpdateClientNotExists() {
        long clientId = 1L;
        ClientDTO updatedDTO = new ClientDTO();
        updatedDTO.setEmail("updated@example.com");

        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> clientService.updateClient(clientId, updatedDTO),
                "Powinien zostać rzucony EntityNotFoundException");

        System.out.println("Test aktualizacji klienta (klient nie istnieje) przeszedł pomyślnie.");
    }

    @Test
    @DisplayName("Test usuwania klienta")
    public void testDeleteClient() {
        Long clientId = 1L;

        clientService.deleteClient(clientId);

        verify(clientRepository, times(1)).deleteById(clientId);

        System.out.println("Test usuwania klienta przeszedł pomyślnie.");
    }

    @Test
    @DisplayName("Test konwersji encji do DTO")
    public void testToDto() {
        Client client = new Client();
        client.setId(1L);
        client.setEmail("client@example.com");
        client.setFirstName("Jan");
        client.setLastName("Kowalski");
        client.setImage("image.jpg");
        Role role = new Role();
        role.setRole("USER");
        client.setRole(role);

        ClientDTO dto = clientService.toDto(client);

        assertEquals(1L, dto.getId(), "ID w DTO powinno być zgodne");
        assertEquals("client@example.com", dto.getEmail(), "Email w DTO powinien być zgodny");
        assertEquals("Jan", dto.getFirstName(), "Imię w DTO powinno być zgodne");
        assertEquals("Kowalski", dto.getLastName(), "Nazwisko w DTO powinno być zgodne");
        assertEquals("image.jpg", dto.getImage(), "Obraz w DTO powinien być zgodny");
        assertEquals("USER", dto.getRole(), "Rola w DTO powinna być zgodna");

        System.out.println("Test konwersji encji do DTO przeszedł pomyślnie.");
    }

}