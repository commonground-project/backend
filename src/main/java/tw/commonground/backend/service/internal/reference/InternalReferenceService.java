package tw.commonground.backend.service.internal.reference;

import org.springframework.stereotype.Service;
import tw.commonground.backend.exception.EntityNotFoundException;
import tw.commonground.backend.service.internal.reference.dto.InternalDetailReferenceResponse;
import tw.commonground.backend.service.internal.reference.dto.InternalReferenceMapper;
import tw.commonground.backend.service.reference.ReferenceEntity;
import tw.commonground.backend.service.reference.ReferenceRepository;
import tw.commonground.backend.service.reference.ReferenceService;
import tw.commonground.backend.shared.tracing.Traced;

import java.util.UUID;

@Traced
@Service
public class InternalReferenceService {

    private final ReferenceService referenceService;
    private final ReferenceRepository referenceRepository;

    public InternalReferenceService(ReferenceService referenceService, ReferenceRepository referenceRepository) {
        this.referenceService = referenceService;
        this.referenceRepository = referenceRepository;
    }

    public InternalDetailReferenceResponse getDetailReference(UUID id) {
//        Long referenceID = id.tolong();
        ReferenceEntity referenceEntity = referenceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reference", "id", id.toString()));


        if(referenceEntity.getDescription() != null) {
            return InternalReferenceMapper.toInternalDetailResponse(referenceEntity);
        }

        String description = referenceService.fetchContentFromFallback(referenceEntity.getUrl());
        referenceEntity.setDescription(description);
        referenceRepository.save(referenceEntity);

        return InternalReferenceMapper.toInternalDetailResponse(referenceEntity);
    }



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

