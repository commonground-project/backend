package tw.commonground.backend.service.internal.profile.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InternalProfileResponse {
    private UUID userId;

    @Builder.Default
    private String gender = "";

    @Builder.Default
    private String occupation = "";

    @Builder.Default
    private String location = "";

    @Builder.Default
    private List<String> browsingTags = Collections.emptyList();

    @Builder.Default
    private List<String> searchKeywords = Collections.emptyList();

    @Builder.Default
    private LocalDateTime createdAt = null;

    @Builder.Default
    private LocalDateTime lastActiveAt = null;

    @Builder.Default
    private Object activityFrequency = Collections.emptyMap();

    @Builder.Default
    private Object userTopIp = Collections.emptyMap();
}
