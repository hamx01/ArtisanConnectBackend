package _11.asktpk.artisanconnectbackend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OAuthPaymentResponseDTO {
    private long issued_at;
    private String scope;
    private String token_type;
    private int expires_in;
    private String client_id;
    private String access_token;
}
