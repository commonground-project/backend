package tw.commonground.backend.service.internal.fact.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tw.commonground.backend.service.internal.reference.dto.InternalDetailReferenceResponse;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@Builder
public class InternalDetailFactResponse {

    private UUID id;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;

    private String title;

    @JsonProperty("publisher_id")
    private UUID authorId;

    @JsonProperty("author_name")
    private String authorName;

    @JsonProperty("author_avatar")
    private String authorAvatar;

    private List<InternalDetailReferenceResponse> references;
}
