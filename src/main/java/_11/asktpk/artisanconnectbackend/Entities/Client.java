package _11.asktpk.artisanconnectbackend.Entities;

import _11.asktpk.artisanconnectbackend.Utils.Enums.Role;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "clients")
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
    private List<Orders> orders;

    public Long getId() {
        return id;
    }

    public void setId(Long idUser) {
        this.id = idUser;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

//    public List<Notice> getNotices() {
//        return notices;
//    }
//
//    public void setNotices(List<Notice> notices) {
//        this.notices = notices;
//    }

    public List<Orders> getOrders() {
        return orders;
    }

    public void setOrders(List<Orders> orders) {
        this.orders = orders;
    }

    // Getters, setters, and constructors
}
