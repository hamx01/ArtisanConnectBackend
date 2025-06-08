package _11.asktpk.artisanconnectbackend.repository;

import _11.asktpk.artisanconnectbackend.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
        Optional<Payment> findByTransactionId(String transactionId);

    List<Payment> findAllByOrderId(Long id);
}
