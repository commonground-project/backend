package tw.commonground.backend.service.reference;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ReferenceResponse {
    private UUID id;
    @JsonProperty("create_at")
    private LocalDateTime createAt;
    private String url;
    private String icon;
    private String title;
}
