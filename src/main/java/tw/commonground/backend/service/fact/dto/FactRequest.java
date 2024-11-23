package tw.commonground.backend.service.fact.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import tw.commonground.backend.service.reference.ReferenceRequest;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Data
public class FactRequest {

    @NotEmpty
    private String title;
    private Set<ReferenceRequest> references = new HashSet<>();

    public Optional<Set<ReferenceRequest>> getReferences() {
        return Optional.ofNullable(references);
    }

}
