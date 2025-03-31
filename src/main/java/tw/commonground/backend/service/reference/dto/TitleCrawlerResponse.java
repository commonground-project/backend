package tw.commonground.backend.service.reference.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TitleCrawlerResponse {
    @JsonProperty("url")
    private String url;

    @JsonProperty("title")
    private String title;

}
