package _11.asktpk.artisanconnectbackend.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "attribute_values")
public class AttributeValues {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_attribute")
    private Attributes attribute;

    private String value;

    // Getters, setters, and constructors
}
