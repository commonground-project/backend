package tw.commonground.backend.service.newcontent.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
@Builder
public class NewcontentResponse {
    private Long userId;
    private UUID objectId;
    private Boolean newcontentStatus;
    private String updatedAt;
}
