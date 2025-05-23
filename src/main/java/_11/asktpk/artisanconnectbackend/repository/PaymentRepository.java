package _11.asktpk.artisanconnectbackend.repository;

import _11.asktpk.artisanconnectbackend.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

}
