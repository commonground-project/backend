package tw.commonground.backend.service.reference.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ContentCrawlerResponse {
    @JsonProperty("url")
    private String url;

    @JsonProperty("content")
    private String content;

}
