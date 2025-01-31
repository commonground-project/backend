package tw.commonground.backend.service.reference.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class ReferenceResponse {
    private UUID id;
    @JsonProperty("create_at")
    private LocalDateTime createAt;
    private String url;
    private String icon;
    private String title;
}
