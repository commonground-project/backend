package tw.commonground.backend.service.urlinfo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class UrlInfoResponse {
    private String title;
    private String icon;
}
