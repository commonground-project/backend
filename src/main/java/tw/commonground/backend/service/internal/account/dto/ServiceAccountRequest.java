package tw.commonground.backend.service.internal.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import tw.commonground.backend.security.UserRole;

@Getter
@Setter
public class ServiceAccountRequest {

    @NotBlank
    private String serviceName;

    @NotNull
    private UserRole role;

}
