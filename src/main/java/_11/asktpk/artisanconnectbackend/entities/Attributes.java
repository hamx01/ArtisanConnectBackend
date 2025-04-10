package _11.asktpk.artisanconnectbackend.entities;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "attributes")
public class Attributes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAttribute;

    private String name;

    @OneToMany(mappedBy = "attribute", cascade = CascadeType.ALL)
    private List<AttributeValues> attributeValues;

    // Getters, setters, and constructors
}
