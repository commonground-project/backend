package tw.commonground.backend.service.reference;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
public class ReferenceResponse {
    @JsonProperty("create_at")
    private LocalDateTime createAt;
    private String url;
    private String icon;
    private String title;
}
