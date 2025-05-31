package _11.asktpk.artisanconnectbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import _11.asktpk.artisanconnectbackend.entities.Role;

@Repository
public interface RolesRepository extends JpaRepository<Role, String> {
    Role findRoleById(Long id);
}
