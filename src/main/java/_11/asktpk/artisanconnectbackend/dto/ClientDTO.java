package _11.asktpk.artisanconnectbackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.Email;

@Getter @Setter
public class ClientDTO {
    private Long id;

    @Email
    @NotBlank
    private String email;
    private String firstName;
    private String lastName;
    private String image;
    private String role;
}
