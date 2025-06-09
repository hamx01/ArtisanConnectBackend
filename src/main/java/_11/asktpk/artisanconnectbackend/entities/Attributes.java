package _11.asktpk.artisanconnectbackend.entities;

import jakarta.persistence.*;
import lombok.Setter;
import lombok.Getter;

import java.util.List;

@Entity
@Table(name = "attributes")
@Getter @Setter
public class Attributes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAttribute;

    private String name;

    @OneToMany(mappedBy = "attribute")
    private List<AttributeValues> attributeValues;
}
