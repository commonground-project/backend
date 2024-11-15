package tw.commonground.backend.service.fact.dao;

import lombok.*;
import tw.commonground.backend.service.reference.ReferenceResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FactResponse {

    private UUID id;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private String title;
    private Long authorId;
    private String authorName;
    private List<ReferenceResponse> references;
}
