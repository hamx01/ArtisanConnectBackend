package _11.asktpk.artisanconnectbackend.Model;

import _11.asktpk.artisanconnectbackend.Utils.Enums.Role;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "clients")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUser;

    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String image; // Optional field
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Notice> notices;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Orders> orders;

    // Getters, setters, and constructors
}
