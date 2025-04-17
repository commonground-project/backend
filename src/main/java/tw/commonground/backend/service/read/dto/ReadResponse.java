package tw.commonground.backend.service.read.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
@Builder
public class ReadResponse {
    private Long userId;
    private UUID objectId;
    private Boolean readStatus;
    private String updatedAt;
}
