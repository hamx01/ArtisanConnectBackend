package _11.asktpk.artisanconnectbackend.entities;

import _11.asktpk.artisanconnectbackend.utils.Enums.Role;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "clients")
@Getter @Setter
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String password;

    private String firstName;

    private String lastName;

    private String image; // Optional field

    @Enumerated(EnumType.STRING)
    private Role role;

//    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
//    private List<Notice> notices;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Order> orders;
}
