package tw.commonground.backend.service.read.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tw.commonground.backend.service.read.entity.ReadObjectType;

import java.util.UUID;

@Getter
@Setter
@ToString
@Builder
public class ReadResponse {
    private Long userId;
    private UUID objectId;
    private ReadObjectType objectType;
    private Boolean readStatus;
    private String updatedAt;
}
