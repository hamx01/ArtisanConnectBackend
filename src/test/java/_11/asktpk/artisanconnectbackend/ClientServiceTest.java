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
        System.out.println("Rozpoczęcie testu: testGetAllClients - Test pobierania wszystkich klientów");

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

        System.out.println("Pobrano listę klientów, liczba elementów: " + clients.size());
        assertEquals(2, clients.size(), "Lista klientów powinna zawierać 2 elementy");
        System.out.println("Pierwszy klient na liście: " + clients.getFirst().getEmail());
        assertEquals("client1@example.com", clients.getFirst().getEmail(), "Email pierwszego klienta powinien być poprawny");

        System.out.println("Test pobierania wszystkich klientów zakończony sukcesem. Zwrócono " + clients.size() + " klientów.");
    }

    @Test
    @DisplayName("Test pobierania klienta po ID - klient istnieje")
    public void testGetClientByIdExists() {
        Long clientId = 1L;
        System.out.println("Rozpoczęcie testu: testGetClientByIdExists - Test pobierania klienta po ID (ID: " + clientId + ")");

        Client client = new Client();
        client.setId(clientId);
        client.setEmail("client@example.com");
        client.setRole(new Role());

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));

        Client retrievedClient = clientService.getClientById(clientId);

        System.out.println("Pobrano klienta o ID: " + (retrievedClient != null ? retrievedClient.getId() : "null"));
        assertNotNull(retrievedClient, "Pobrany klient nie powinien być null");
        assertEquals(clientId, retrievedClient.getId(), "ID klienta powinno być zgodne");

        System.out.println("Test pobierania klienta po ID (ID: " + clientId + ") zakończony sukcesem. Znaleziono klienta: " + retrievedClient.getEmail());
    }

    @Test
    @DisplayName("Test pobierania klienta po ID - klient nie istnieje")
    public void testGetClientByIdNotExists() {
        Long clientId = 1L;
        System.out.println("Rozpoczęcie testu: testGetClientByIdNotExists - Test pobierania nieistniejącego klienta (ID: " + clientId + ")");

        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        Client retrievedClient = clientService.getClientById(clientId);

        System.out.println("Próba pobrania nieistniejącego klienta zwróciła: " + retrievedClient);
        assertNull(retrievedClient, "Pobrany klient powinien być null, gdy nie istnieje");

        System.out.println("Test pobierania nieistniejącego klienta (ID: " + clientId + ") zakończony sukcesem. Zwrócono null zgodnie z oczekiwaniami.");
    }

    @Test
    @DisplayName("Test pobierania klienta po emailu")
    public void testGetClientByEmail() {
        String email = "client@example.com";
        System.out.println("Rozpoczęcie testu: testGetClientByEmail - Test pobierania klienta po emailu (" + email + ")");

        Client client = new Client();
        client.setEmail(email);

        when(clientRepository.findByEmail(email)).thenReturn(client);

        Client retrievedClient = clientService.getClientByEmail(email);

        System.out.println("Pobrano klienta o emailu: " + (retrievedClient != null ? retrievedClient.getEmail() : "null"));
        assertNotNull(retrievedClient, "Pobrany klient nie powinien być null");
        assertEquals(email, retrievedClient.getEmail(), "Email klienta powinien być zgodny");

        System.out.println("Test pobierania klienta po emailu (" + email + ") zakończony sukcesem.");
    }

    @Test
    @DisplayName("Test dodawania klienta")
    public void testAddClient() {
        System.out.println("Rozpoczęcie testu: testAddClient - Test dodawania nowego klienta");

        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setEmail("newclient@example.com");
        clientDTO.setRole("USER");

        Client client = new Client();
        client.setEmail("newclient@example.com");
        client.setRole(new Role());

        when(clientRepository.save(any(Client.class))).thenReturn(client);

        ClientDTO addedClient = clientService.addClient(clientDTO);

        System.out.println("Dodano nowego klienta: " + (addedClient != null ? addedClient.getEmail() : "null"));
        assertNotNull(addedClient, "Dodany klient nie powinien być null");
        assertEquals("newclient@example.com", addedClient.getEmail(), "Email dodanego klienta powinien być poprawny");

        System.out.println("Test dodawania klienta zakończony sukcesem. Dodano klienta: " + addedClient.getEmail());
    }

    @Test
    @DisplayName("Test aktualizacji klienta")
    public void testUpdateClient() {
        Long clientId = 1L;
        System.out.println("Rozpoczęcie testu: testUpdateClient - Test aktualizacji klienta (ID: " + clientId + ")");

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

        System.out.println("Zaktualizowano klienta. Nowy email: " + updatedClient.getEmail());
        assertEquals("updated@example.com", updatedClient.getEmail(), "Email klienta powinien być zaktualizowany");

        System.out.println("Test aktualizacji klienta (ID: " + clientId + ") zakończony sukcesem. Nowy email: " + updatedClient.getEmail());
    }

    @Test
    @DisplayName("Test aktualizacji klienta - klient nie istnieje")
    public void testUpdateClientNotExists() {
        long clientId = 1L;
        System.out.println("Rozpoczęcie testu: testUpdateClientNotExists - Test aktualizacji nieistniejącego klienta (ID: " + clientId + ")");

        ClientDTO updatedDTO = new ClientDTO();
        updatedDTO.setEmail("updated@example.com");

        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        System.out.println("Oczekiwanie na EntityNotFoundException...");
        assertThrows(EntityNotFoundException.class, () -> clientService.updateClient(clientId, updatedDTO),
                "Powinien zostać rzucony EntityNotFoundException");

        System.out.println("Test aktualizacji nieistniejącego klienta (ID: " + clientId + ") zakończony sukcesem. Rzucono wyjątek EntityNotFoundException zgodnie z oczekiwaniami.");
    }

    @Test
    @DisplayName("Test usuwania klienta")
    public void testDeleteClient() {
        Long clientId = 1L;
        System.out.println("Rozpoczęcie testu: testDeleteClient - Test usuwania klienta (ID: " + clientId + ")");

        clientService.deleteClient(clientId);

        verify(clientRepository, times(1)).deleteById(clientId);
        System.out.println("Weryfikacja: metoda deleteById została wywołana 1 raz z ID: " + clientId);

        System.out.println("Test usuwania klienta (ID: " + clientId + ") zakończony sukcesem.");
    }

    @Test
    @DisplayName("Test konwersji encji do DTO")
    public void testToDto() {
        System.out.println("Rozpoczęcie testu: testToDto - Test konwersji encji Client do ClientDTO");

        Client client = new Client();
        client.setId(1L);
        client.setEmail("client@example.com");
        client.setFirstName("Jan");
        client.setLastName("Kowalski");
        client.setImage("image.jpg");
        Role role = new Role();
        role.setRole("USER");
        client.setRole(role);

        System.out.println("Przygotowano encję Client do konwersji:");
        System.out.println("ID: " + client.getId());
        System.out.println("Email: " + client.getEmail());
        System.out.println("Imię: " + client.getFirstName());
        System.out.println("Nazwisko: " + client.getLastName());
        System.out.println("Obraz: " + client.getImage());
        System.out.println("Rola: " + client.getRole().getRole());

        ClientDTO dto = clientService.toDto(client);

        System.out.println("Wynik konwersji do DTO:");
        System.out.println("ID: " + dto.getId());
        System.out.println("Email: " + dto.getEmail());
        System.out.println("Imię: " + dto.getFirstName());
        System.out.println("Nazwisko: " + dto.getLastName());
        System.out.println("Obraz: " + dto.getImage());
        System.out.println("Rola: " + dto.getRole());

        assertEquals(1L, dto.getId(), "ID w DTO powinno być zgodne");
        assertEquals("client@example.com", dto.getEmail(), "Email w DTO powinien być zgodny");
        assertEquals("Jan", dto.getFirstName(), "Imię w DTO powinno być zgodne");
        assertEquals("Kowalski", dto.getLastName(), "Nazwisko w DTO powinno być zgodne");
        assertEquals("image.jpg", dto.getImage(), "Obraz w DTO powinien być zgodny");
        assertEquals("USER", dto.getRole(), "Rola w DTO powinna być zgodna");

        System.out.println("Test konwersji encji do DTO zakończony sukcesem. Wszystkie pola zostały poprawnie zmapowane.");
    }
}