package tw.commonground.backend.service.internal.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tw.commonground.backend.security.UserRole;

@Getter
@Setter
@ToString
public class ServiceAccountRequest {

    @NotBlank
    private String name;

    @NotNull
    private UserRole role;

}
