package tw.commonground.backend.service.reference.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ReferenceResponseForAI {
    private UUID id;
    @JsonProperty("create_at")
    private String createAt;
    private String url;
    private String icon;
    private String title;
    private String description; // crawl description
    // TODO: need to create a mapper to map the response to the request
}

