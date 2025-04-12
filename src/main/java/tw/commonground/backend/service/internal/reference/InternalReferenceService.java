package tw.commonground.backend.service.internal.reference;

import tw.commonground.backend.service.internal.reference.dto.InternalDetailReferenceResponse;
import tw.commonground.backend.service.internal.reference.dto.InternalReferenceMapper;
import tw.commonground.backend.service.reference.ReferenceEntity;
import tw.commonground.backend.service.reference.ReferenceRepository;
import tw.commonground.backend.service.reference.ReferenceService;

public class InternalReferenceService {

    ReferenceService referenceService;
    ReferenceRepository referenceRepository;

    public void createDescriptionForReference(ReferenceEntity referenceEntity) {
        String url = referenceEntity.getUrl();
        if(referenceEntity.getDescription() != null) {
            return;
//            return InternalReferenceMapper.toInternalDetailResponse(referenceEntity);
        }

        String description = referenceService.fetchContentFromFallback(url);

        referenceEntity.setDescription(description);
        referenceRepository.save(referenceEntity);
//        return InternalReferenceMapper.toInternalDetailResponse(referenceEntity);
    }
}

