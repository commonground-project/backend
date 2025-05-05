package tw.commonground.backend.service.viewpoint.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tw.commonground.backend.shared.entity.Preference;

import java.util.UUID;


@Getter
@Setter
@Builder
@ToString
public class ViewpointPreferenceResponse {
    private String id;

    private Preference preference;
}
