package _11.asktpk.artisanconnectbackend.repository;

import _11.asktpk.artisanconnectbackend.entities.AttributeValues;
import _11.asktpk.artisanconnectbackend.entities.Attributes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AttributeValuesRepository extends JpaRepository<AttributeValues, Long> {

    Optional<AttributeValues> findByAttributeAndValue(Attributes attribute, String value);
}
