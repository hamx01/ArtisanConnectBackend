package _11.asktpk.artisanconnectbackend;

import _11.asktpk.artisanconnectbackend.controller.ClientController;
import _11.asktpk.artisanconnectbackend.dto.ClientDTO;
import _11.asktpk.artisanconnectbackend.service.ClientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ClientControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ClientService clientService;

    @InjectMocks
    private ClientController clientController;

    private ClientDTO sampleClientDTO;

    @BeforeEach
    void setUp() {
        System.out.println("Inicjalizacja konfiguracji testu...");
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(clientController).build();

        sampleClientDTO = new ClientDTO();
        sampleClientDTO.setId(1L);
        sampleClientDTO.setEmail("test@example.com");
        sampleClientDTO.setFirstName("John");
        sampleClientDTO.setLastName("Doe");
        sampleClientDTO.setRole("USER");
        System.out.println("Konfiguracja testu zakończona z przykładowym klientem: " + sampleClientDTO);
    }

    @Test
    @DisplayName("Powinien zwrócić listę klientów")
    void getAllClients_ShouldReturnListOfClients() throws Exception {
        System.out.println("Uruchamianie testu: getAllClients_ShouldReturnListOfClients");

        List<ClientDTO> clients = Collections.singletonList(sampleClientDTO);
        when(clientService.getAllClients()).thenReturn(clients);
        System.out.println("Konfiguracja mocka: clientService.getAllClients() will return " + clients);

        mockMvc.perform(get("/api/v1/clients/get/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].email").value("test@example.com"));

        verify(clientService, times(1)).getAllClients();
        System.out.println("Test zaliczony: Successfully retrieved list of clients");
    }

    @Test
    @DisplayName("Powinien utworzyć nowego klienta")
    void addClient_WhenClientNotExists_ShouldCreateClient() throws Exception {
        System.out.println("Uruchamianie testu: addClient_WhenClientNotExists_ShouldCreateClient");

        when(clientService.clientExists(anyLong())).thenReturn(false);
        when(clientService.addClient(any(ClientDTO.class))).thenReturn(sampleClientDTO);
        System.out.println("Konfiguracja mocka: clientService.addClient() will return " + sampleClientDTO);

        mockMvc.perform(post("/api/v1/clients/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleClientDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));

        verify(clientService, times(1)).addClient(any(ClientDTO.class));
        System.out.println("Test zaliczony: Successfully created new client");
    }

    @Test
    @DisplayName("Powinien zwrócić 409 gdy klient istnieje")
    void addClient_WhenClientExists_ShouldReturnConflict() throws Exception {
        System.out.println("Uruchamianie testu: addClient_WhenClientExists_ShouldReturnConflict");

        when(clientService.clientExists(anyLong())).thenReturn(true);
        System.out.println("Konfiguracja mocka: clientService.clientExists() will return true");

        mockMvc.perform(post("/api/v1/clients/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleClientDTO)))
                .andExpect(status().isConflict());

        verify(clientService, times(0)).addClient(any(ClientDTO.class));
        System.out.println("Test zaliczony: Correctly returned 409 for existing client");
    }

    @Test
    @DisplayName("Powinien zaktualizować istniejącego klienta")
    void updateClient_WhenClientExists_ShouldUpdateClient() throws Exception {
        System.out.println("Uruchamianie testu: updateClient_WhenClientExists_ShouldUpdateClient");

        when(clientService.clientExists(1L)).thenReturn(true);
        when(clientService.updateClient(anyLong(), any(ClientDTO.class))).thenReturn(sampleClientDTO);
        System.out.println("Konfiguracja mocka: clientService.updateClient() will return " + sampleClientDTO);

        mockMvc.perform(put("/api/v1/clients/edit/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleClientDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(clientService, times(1)).updateClient(anyLong(), any(ClientDTO.class));
        System.out.println("Test zaliczony: Successfully updated client");
    }

    @Test
    @DisplayName("Powinien zwrócić 404 gdy klient nie istnieje")
    void updateClient_WhenClientNotExists_ShouldReturnNotFound() throws Exception {
        System.out.println("Uruchamianie testu: updateClient_WhenClientNotExists_ShouldReturnNotFound");

        when(clientService.clientExists(1L)).thenReturn(false);
        System.out.println("Konfiguracja mocka: clientService.clientExists(1L) will return false");

        mockMvc.perform(put("/api/v1/clients/edit/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleClientDTO)))
                .andExpect(status().isNotFound());

        verify(clientService, times(0)).updateClient(anyLong(), any(ClientDTO.class));
        System.out.println("Test zaliczony: Correctly returned 404 for non-existent client");
    }

    @Test
    @DisplayName("Powinien usunąć istniejącego klienta")
    void deleteClient_WhenClientExists_ShouldDeleteClient() throws Exception {
        System.out.println("Uruchamianie testu: deleteClient_WhenClientExists_ShouldDeleteClient");

        when(clientService.clientExists(1L)).thenReturn(true);
        doNothing().when(clientService).deleteClient(1L);
        System.out.println("Konfiguracja mocka: clientService.deleteClient(1L) will do nothing");

        mockMvc.perform(delete("/api/v1/clients/delete/1"))
                .andExpect(status().isOk());

        verify(clientService, times(1)).deleteClient(1L);
        System.out.println("Test zaliczony: Successfully deleted client");
    }

    @Test
    @DisplayName("Powinien zwrócić 404 gdy klient nie istnieje")
    void deleteClient_WhenClientNotExists_ShouldReturnNotFound() throws Exception {
        System.out.println("Uruchamianie testu: deleteClient_WhenClientNotExists_ShouldReturnNotFound");

        when(clientService.clientExists(1L)).thenReturn(false);
        System.out.println("Konfiguracja mocka: clientService.clientExists(1L) will return false");

        mockMvc.perform(delete("/api/v1/clients/delete/1"))
                .andExpect(status().isNotFound());

        verify(clientService, times(0)).deleteClient(anyLong());
        System.out.println("Test zaliczony: Correctly returned 404 for non-existent client");
    }
}