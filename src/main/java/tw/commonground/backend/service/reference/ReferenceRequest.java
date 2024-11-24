package tw.commonground.backend.service.reference;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReferenceRequest {
    @NotBlank(message = "It should have a url")
    private String url;
}
