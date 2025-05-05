package tw.commonground.backend.service.viewpoint.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tw.commonground.backend.shared.entity.Preference;

import java.util.UUID;

@Getter
@Setter
@ToString
public class ViewpointPreferenceRequest {
    private UUID id;

    private Preference preference;
}
