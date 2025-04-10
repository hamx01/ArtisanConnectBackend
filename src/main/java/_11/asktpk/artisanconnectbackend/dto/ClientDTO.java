package _11.asktpk.artisanconnectbackend.dto;

import lombok.Getter;
import lombok.Setter;

import _11.asktpk.artisanconnectbackend.utils.Enums.Role;

@Getter @Setter
public class ClientDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String image;
    private Role role;
}
