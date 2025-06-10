package _11.asktpk.artisanconnectbackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.Email;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
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
