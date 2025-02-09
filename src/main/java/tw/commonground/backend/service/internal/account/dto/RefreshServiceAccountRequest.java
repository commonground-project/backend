package tw.commonground.backend.service.internal.account.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshServiceAccountRequest {
    @NotBlank
    private String serviceName;
}
