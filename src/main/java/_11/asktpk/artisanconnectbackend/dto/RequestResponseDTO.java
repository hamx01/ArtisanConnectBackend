package _11.asktpk.artisanconnectbackend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RequestResponseDTO {
    public String message;

    public RequestResponseDTO(String message) {
        this.message = message;
    }
}
