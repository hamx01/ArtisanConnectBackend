package _11.asktpk.artisanconnectbackend.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "global_variables")
public class GlobalVariables {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String value;

    // Getters, setters, and constructors
}
