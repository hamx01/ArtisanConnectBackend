package _11.asktpk.artisanconnectbackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ClientRegistrationDTO {
    @Email
    @NotBlank
    private String email;
    private String firstName;
    private String lastName;
    private String password;
}
