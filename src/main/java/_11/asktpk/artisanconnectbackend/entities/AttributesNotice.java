package _11.asktpk.artisanconnectbackend.entities;

import jakarta.persistence.*;
import lombok.Setter;
import lombok.Getter;

@Entity
@Table(name = "attributes_notice")
@Getter @Setter
public class AttributesNotice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long notice_id;

    @ManyToOne
    @JoinColumn(name = "id_value")
    private AttributeValues attributeValue;
}