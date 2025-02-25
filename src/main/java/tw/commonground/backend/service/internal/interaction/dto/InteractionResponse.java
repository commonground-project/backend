package tw.commonground.backend.service.internal.interaction.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class InteractionResponse {

    private String timestamp;

    @JsonProperty("interaction_id")
    private String id;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("related_object")
    private RelatedObject relatedObject;

    private String type;

    @JsonProperty
    @JsonInclude(Include.NON_NULL)
    private String content;

}
