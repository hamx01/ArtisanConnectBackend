package _11.asktpk.artisanconnectbackend.Repository;

import _11.asktpk.artisanconnectbackend.Entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
}
