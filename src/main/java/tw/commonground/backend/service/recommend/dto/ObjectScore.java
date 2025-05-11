package tw.commonground.backend.service.recommend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ObjectScore {
    @JsonProperty("item_id")
    private UUID objectId;

    private Float similarity;
}
