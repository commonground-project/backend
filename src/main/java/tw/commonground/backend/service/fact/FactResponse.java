package tw.commonground.backend.service.fact;

import lombok.*;

import java.time.LocalDateTime;
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

}
