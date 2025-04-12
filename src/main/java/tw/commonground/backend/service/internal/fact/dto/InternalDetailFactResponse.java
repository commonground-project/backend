package tw.commonground.backend.service.internal.fact.dto;

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

    private String createdAt;

    private String updatedAt;

    private String title;

    private UUID authorId;

    private String authorName;

    private String authorAvatar;

    private List<InternalDetailReferenceResponse> references;
}
