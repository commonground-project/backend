package tw.commonground.backend.service.jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RefreshTokenResponse {

    private String refreshToken;

    private Long expirationTime;

    private String accessToken;
}
