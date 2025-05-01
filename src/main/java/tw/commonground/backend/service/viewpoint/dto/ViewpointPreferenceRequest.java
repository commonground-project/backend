package tw.commonground.backend.service.viewpoint.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tw.commonground.backend.shared.entity.Preference;

@Getter
@Setter
@ToString
public class ViewpointPreferenceRequest {
    private Preference preference;
}