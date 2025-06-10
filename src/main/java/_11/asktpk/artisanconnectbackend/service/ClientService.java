package _11.asktpk.artisanconnectbackend.service;

import _11.asktpk.artisanconnectbackend.dto.ClientDTO;
import _11.asktpk.artisanconnectbackend.dto.ClientRegistrationDTO;
import _11.asktpk.artisanconnectbackend.entities.Client;
import _11.asktpk.artisanconnectbackend.entities.Role;
import _11.asktpk.artisanconnectbackend.repository.ClientRepository;
import _11.asktpk.artisanconnectbackend.repository.RolesRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientService {
    private final ClientRepository clientRepository;
    private final RolesRepository rolesRepository;

    public ClientService(ClientRepository clientRepository, RolesRepository rolesRepository) {
        this.clientRepository = clientRepository;
        this.rolesRepository = rolesRepository;
    }

    public ClientDTO toDto(Client client) {
        if(client == null) {
            return null;
        }

        ClientDTO dto = new ClientDTO();

        dto.setId(client.getId());
        dto.setFirstName(client.getFirstName());
        dto.setLastName(client.getLastName());
        dto.setEmail(client.getEmail());
        dto.setRole(client.getRole().getRole());
        dto.setImage(client.getImage());

        return dto;
    }

    public Client fromDto(ClientDTO dto) {
        Client client = new Client();
        Role rola;

        if (clientRepository.findById(dto.getId()).isPresent()) {
            rola = clientRepository.findById(dto.getId()).get().getRole();
        } else {
            rola = new Role();
            rola.setRole("USER");
        }

        client.setId(dto.getId());
        client.setFirstName(dto.getFirstName());
        client.setLastName(dto.getLastName());
        client.setEmail(dto.getEmail());
        client.setRole(rola);
        client.setImage(dto.getImage());

        return client;
    }

    private Client fromDto(ClientRegistrationDTO dto) {
           Client client = new Client();

           client.setFirstName(dto.getFirstName());
           client.setLastName(dto.getLastName());
           client.setEmail(dto.getEmail());
           client.setPassword(dto.getPassword());
           return client;
    }

    public List<ClientDTO> getAllClients() {
        List<Client> clients = clientRepository.findAll();
        return clients.stream().map(this::toDto).toList();
    }

    public Client getClientById(Long id) {
        return clientRepository.findById(id).orElse(null);
    }

    public ClientDTO getClientByIdDTO(Long id) {
        return toDto(clientRepository.findById(id).orElse(null));
    }

    public Client getClientByEmail(String email) {
        return clientRepository.findByEmail(email);
    }

    public Role getUserRole() {
        return rolesRepository.findRoleByRole("USER");
    }

    public boolean clientExists(Long id) {
        return clientRepository.existsById(id);
    }

    public ClientDTO addClient(ClientDTO clientDTO) {
        return toDto(clientRepository.save(fromDto(clientDTO)));
    }

    public Client saveClientToDB(Client client) {
        return clientRepository.save(client);
    }

    public ClientDTO updateClient(long id, ClientDTO clientDTO) {
        Client existingClient = clientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono ogłoszenia o ID: " + id));

        Role newRole = rolesRepository.findRoleByRole(clientDTO.getRole());

        existingClient.setEmail(clientDTO.getEmail());
        existingClient.setFirstName(clientDTO.getFirstName());
        existingClient.setLastName(clientDTO.getLastName());
        existingClient.setImage(clientDTO.getImage());
        existingClient.setRole(newRole);

        return toDto(clientRepository.save(existingClient));
    }

    public void deleteClient(Long id) {
        clientRepository.deleteById(id);
    }

    public ClientDTO registerClient(Client client) {
        client.setRole(getUserRole()); // ID 1 - USER role
        return toDto(clientRepository.save(client));
    }
}
