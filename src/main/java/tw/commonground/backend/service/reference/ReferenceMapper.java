package tw.commonground.backend.service.reference;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class ReferenceMapper {
    public ReferenceResponse toResponse(ReferenceEntity referenceEntity) {

        String encodedUrl = URLEncoder.encode(referenceEntity.getUrl(), StandardCharsets.UTF_8);
        String encodedIcon;
        if (referenceEntity.getFavicon().isBlank()) {
            encodedIcon = "";
        } else {
            encodedIcon = URLEncoder.encode(referenceEntity.getFavicon(), StandardCharsets.UTF_8);
        }

        return new ReferenceResponse(
                referenceEntity.getId(),
                referenceEntity.getCreateAt(),
                encodedUrl,
                encodedIcon,
                referenceEntity.getTitle());
    }
}
