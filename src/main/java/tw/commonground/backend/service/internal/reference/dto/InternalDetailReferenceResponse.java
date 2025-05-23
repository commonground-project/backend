package tw.commonground.backend.service.internal.reference.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
public class InternalDetailReferenceResponse {
    private UUID id;
    @JsonProperty("created_at")
    private String createdAt;
    private String url;
    private String icon;
    private String title;
    private String description; // crawl description
 }
