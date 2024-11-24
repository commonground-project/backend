package tw.commonground.backend.service.reference;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReferenceRequest {
    @NotBlank(message = "It should have a url")
    private String url;
}
