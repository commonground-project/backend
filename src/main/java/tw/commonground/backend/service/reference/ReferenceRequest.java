package tw.commonground.backend.service.reference;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReferenceRequest {
    @NotBlank(message = "url is required")
    private String url;
}
