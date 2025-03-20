package tw.commonground.backend.service.reference.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class FallbackResponse {
    @JsonProperty("url")
    private String url;

    @JsonProperty("title")
    private String title;

}
