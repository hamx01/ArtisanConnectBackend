package _11.asktpk.artisanconnectbackend.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
public class EmailDTO {
    @Email(message = "Podaj poprawny adres email")
    @NotBlank(message = "Adres email nie może być pusty")
    private String to;

    @NotBlank(message = "Temat nie może być pusty")
    private String subject;

    @NotBlank(message = "Treść nie może być pusta")
    private String body;
}