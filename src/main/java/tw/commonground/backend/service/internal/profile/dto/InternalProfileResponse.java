package tw.commonground.backend.service.internal.profile.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("user_id")
    private UUID userUuid;

    @Builder.Default
    private String gender = "";

    @Builder.Default
    private String occupation = "";

    @Builder.Default
    private String location = "";

    @Builder.Default
    @JsonProperty("browsing_tags")
    private List<String> browsingTags = Collections.emptyList();

    @Builder.Default
    @JsonProperty("search_keywords")
    private List<String> searchKeywords = Collections.emptyList();

    @Builder.Default
    @JsonProperty("created_at")
    private LocalDateTime createdAt = null;

    @Builder.Default
    @JsonProperty("last_active_at")
    private LocalDateTime lastActiveAt = null;

    @Builder.Default
    @JsonProperty("activity_frequency")
    private Object activityFrequency = Collections.emptyMap();

    @Builder.Default
    @JsonProperty("user_top_ip")
    private Object userTopIp = Collections.emptyMap();
}
