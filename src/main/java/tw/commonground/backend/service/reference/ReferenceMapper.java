package tw.commonground.backend.service.reference;

public class ReferenceMapper {
    public ReferenceResponse toResponse(ReferenceEntity referenceEntity) {
        return new ReferenceResponse(
                referenceEntity.getCreateAt(),
                referenceEntity.getUrl(),
                referenceEntity.getFavicon(),
                referenceEntity.getTitle());
    }
}
