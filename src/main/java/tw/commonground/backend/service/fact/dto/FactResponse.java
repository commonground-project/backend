package tw.commonground.backend.service.fact.dto;

import lombok.*;
import tw.commonground.backend.service.reference.ReferenceResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class FactResponse {

    private UUID id;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;

    private String title;

    private UUID authorId;

    private String authorName;

    private List<ReferenceResponse> references;
}
