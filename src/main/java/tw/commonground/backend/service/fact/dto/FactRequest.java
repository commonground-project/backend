package tw.commonground.backend.service.fact.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import tw.commonground.backend.service.reference.dto.ReferenceRequest;

import java.util.*;

@Getter
@Setter
@ToString
public class FactRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @Valid
    private Set<ReferenceRequest> references = new HashSet<>();

    public Optional<Set<ReferenceRequest>> getReferences() {
        return Optional.ofNullable(references);
    }

    public List<String> getUrls() {
        List<String> urls = new ArrayList<>();
        getReferences().ifPresent(references ->
                urls.addAll(references.stream().map(ReferenceRequest::getUrl).toList()));
        return urls;
    }
}
