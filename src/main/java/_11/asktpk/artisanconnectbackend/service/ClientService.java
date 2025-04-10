package _11.asktpk.artisanconnectbackend.service;

import _11.asktpk.artisanconnectbackend.dto.ClientDTO;
import _11.asktpk.artisanconnectbackend.entities.Client;
import _11.asktpk.artisanconnectbackend.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientService {
    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public ClientDTO toDto(Client client) {
        ClientDTO dto = new ClientDTO();

        dto.setId(client.getId());
        dto.setFirstName(client.getFirstName());
        dto.setLastName(client.getLastName());
        dto.setEmail(client.getEmail());
        dto.setRole(client.getRole());
        dto.setImage(client.getImage());

        return dto;
    }

    public Client fromDto(ClientDTO dto) {
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

    public boolean getByID(Long id) {
        return clientRepository.existsById(id);
    }

}
