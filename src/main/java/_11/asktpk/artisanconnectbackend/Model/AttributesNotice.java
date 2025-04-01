package _11.asktpk.artisanconnectbackend.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "attributes_notice")
public class AttributesNotice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_notice")
    private Notice notice;

    @ManyToOne
    @JoinColumn(name = "id_value")
    private AttributeValues attributeValue;

    // Getters, setters, and constructors
}