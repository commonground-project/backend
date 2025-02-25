package tw.commonground.backend.service.issue.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class IssueRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    private String insight;

    private List<UUID> facts;

    public String getInsight() {
        if (insight == null) {
            return "";
        }

        return insight;
    }

    public List<UUID> getFacts() {
        if (facts == null) {
            return List.of();
        }

        return facts;
    }
}
