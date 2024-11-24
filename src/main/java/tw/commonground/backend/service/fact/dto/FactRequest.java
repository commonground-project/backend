package tw.commonground.backend.service.fact.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import tw.commonground.backend.service.reference.ReferenceRequest;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Getter
@Setter
public class FactRequest {

    @NotBlank(message = "It should have a title")
    private String title;
    private Set<ReferenceRequest> references = new HashSet<>();

    public Optional<Set<ReferenceRequest>> getReferences() {
        return Optional.ofNullable(references);
    }

}
