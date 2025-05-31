package _11.asktpk.artisanconnectbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class AuthResponseDTO {
    private Long user_id;
    private String user_role;
    private String token;
}
