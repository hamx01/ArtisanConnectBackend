package _11.asktpk.artisanconnectbackend.controller;


import _11.asktpk.artisanconnectbackend.dto.ClientDTO;
import _11.asktpk.artisanconnectbackend.repository.ClientRepository;
import _11.asktpk.artisanconnectbackend.service.ClientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clients")
public class ClientController {
    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/get/all")
    public List<ClientDTO> getAllClients() {
        return clientService.getAllClients();
    }

    @GetMapping("/get/{id}")
    public ResponseEntity getClientById(@PathVariable long id) {
        if(clientService.getClientById(id) != null) {
            return new ResponseEntity(clientService.getClientById(id), HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    // TODO: do zrobienia walidacja danych
    @PutMapping("/edit/{id}")
    public ResponseEntity updateClient(@PathVariable("id") long id, @RequestBody ClientDTO clientDTO) {
        if(clientService.clientExists(id)) {
            return new ResponseEntity<>(clientService.updateClient(id, clientDTO),HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteClient(@PathVariable("id") long id) {
        if(clientService.clientExists(id)) {
            clientService.deleteClient(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
