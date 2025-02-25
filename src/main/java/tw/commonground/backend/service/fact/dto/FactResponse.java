package tw.commonground.backend.service.fact.dto;

import lombok.*;
import tw.commonground.backend.service.reference.dto.ReferenceResponse;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@Builder
public class FactResponse {

    private UUID id;

    private String createdAt;

    private String updatedAt;

    private String title;

    private UUID authorId;

    private String authorName;

    private String authorAvatar;

    private List<ReferenceResponse> references;
}
