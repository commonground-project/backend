package tw.commonground.backend.service.reference;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ReferenceRequest {
    @NotEmpty
    private String url;
}
