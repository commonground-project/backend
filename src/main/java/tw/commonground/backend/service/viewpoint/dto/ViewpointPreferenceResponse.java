package tw.commonground.backend.service.viewpoint.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tw.commonground.backend.shared.entity.Preference;


@Getter
@Setter
@Builder
@ToString
public class ViewpointPreferenceResponse {
    private Preference preference;
}
