package tw.commonground.backend.service.internal.account.dto;

import lombok.Getter;
import lombok.ToString;
import tw.commonground.backend.security.UserRole;

import java.util.UUID;

@Getter
@ToString
public class CreatedServiceAccountResponse extends ServiceAccountResponse {

    private final String token;

    public CreatedServiceAccountResponse(UUID id, String name, UserRole role, String token) {
        super(id, name, role);
        this.token = token;
    }
}
