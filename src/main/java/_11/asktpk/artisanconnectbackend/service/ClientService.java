package _11.asktpk.artisanconnectbackend.service;

import _11.asktpk.artisanconnectbackend.dto.ClientDTO;
import _11.asktpk.artisanconnectbackend.entities.Client;
import _11.asktpk.artisanconnectbackend.repository.ClientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientService {
    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    private ClientDTO toDto(Client client) {
        ClientDTO dto = new ClientDTO();

        dto.setId(client.getId());
        dto.setFirstName(client.getFirstName());
        dto.setLastName(client.getLastName());
        dto.setEmail(client.getEmail());
        dto.setRole(client.getRole());
        dto.setImage(client.getImage());

        return dto;
    }

    private Client fromDto(ClientDTO dto) {
        Client client = new Client();

        client.setId(dto.getId());
        client.setFirstName(dto.getFirstName());
        client.setLastName(dto.getLastName());
        client.setEmail(dto.getEmail());
        client.setRole(dto.getRole());
        client.setImage(dto.getImage());

        return client;
    }

    public List<ClientDTO> getAllClients() {
        List<Client> clients = clientRepository.findAll();
        return clients.stream().map(this::toDto).toList();
    }

    public Client getClientById(Long id) {
        return clientRepository.findById(id).orElse(null);
    }

    public boolean clientExists(Long id) {
        return clientRepository.existsById(id);
    }

    public ClientDTO addClient(ClientDTO clientDTO) {
        return toDto(clientRepository.save(fromDto(clientDTO)));
    }

    public ClientDTO updateClient(long id, ClientDTO clientDTO) {
        Client existingClient = clientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono ogłoszenia o ID: " + id));

        existingClient.setEmail(clientDTO.getEmail());
        existingClient.setFirstName(clientDTO.getFirstName());
        existingClient.setLastName(clientDTO.getLastName());
        existingClient.setImage(clientDTO.getImage());
        existingClient.setRole(clientDTO.getRole());

        return toDto(clientRepository.save(existingClient));
    }

    public void deleteClient(Long id) {
        clientRepository.deleteById(id);
    }
}
