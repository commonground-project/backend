package tw.commonground.backend.service.read.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tw.commonground.backend.service.read.entity.ReadObjectType;

@Getter
@Setter
@ToString
@Builder
public class ReadRequest {
    private ReadObjectType objectType;
    private Boolean readStatus;
}
