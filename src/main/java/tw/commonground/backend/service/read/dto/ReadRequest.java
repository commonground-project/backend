package tw.commonground.backend.service.read.dto;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReadRequest {
    private Boolean readStatus;
}
