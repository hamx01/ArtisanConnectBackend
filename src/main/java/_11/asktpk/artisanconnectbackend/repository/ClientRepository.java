package _11.asktpk.artisanconnectbackend.repository;

import _11.asktpk.artisanconnectbackend.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Client findByEmail(String email);
}
