package tw.commonground.backend.service.fact;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tw.commonground.backend.exception.EntityNotFoundException;
import tw.commonground.backend.service.fact.dto.FactMapper;
import tw.commonground.backend.service.fact.dto.FactRequest;
import tw.commonground.backend.service.fact.dto.FactResponse;
import tw.commonground.backend.service.fact.entity.FactEntity;
import tw.commonground.backend.service.fact.entity.FactRepository;
import tw.commonground.backend.service.reference.*;
import tw.commonground.backend.service.pagination.PaginationMapper;
import tw.commonground.backend.service.pagination.WrappedPaginationResponse;

import java.util.*;

@Service
public class FactService {

    private final FactRepository factRepository;

    private final ReferenceRepository referenceRepository;

    private final FactMapper factMapper = new FactMapper();
    private final ReferenceMapper referenceMapper = new ReferenceMapper();
    private final PaginationMapper paginationMapper = new PaginationMapper();

    public FactService(FactRepository factRepository, ReferenceRepository referenceRepository) {
        this.factRepository = factRepository;
        this.referenceRepository = referenceRepository;
    }

    public WrappedPaginationResponse<List<FactResponse>> getFacts(Pageable pageable) {
        Page<FactEntity> pageFacts = factRepository.findAll(pageable);

        List<FactResponse> factResponses = pageFacts.getContent()
                .stream()
                .map(factMapper::toResponse)
                .toList();

        return new WrappedPaginationResponse<>(factResponses, paginationMapper.toResponse(pageFacts));
    }


    public FactResponse getFact(UUID id) {

        FactEntity factEntity = factRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Fact", "id", id.toString())
        );

        return factMapper.toResponse(factEntity);
    }

    public FactResponse createFact(FactRequest factRequest) {
        //TODO: Map to logged in user
        FactEntity factEntity = FactEntity.builder()
                .title(factRequest.getTitle())
                .authorId(1L)
                .authorName("aaa")
                .authorAvatar("asd")
                .references(new HashSet<>())
                .build();

        Set<ReferenceEntity> referenceEntities = parseReferenceEntity(factRequest.getUrls());
        factEntity.setReferences(referenceEntities);

        return factMapper.toResponse(factRepository.save(factEntity));
    }

    // sameUrls represent the urls which already save in ReferenceEntity
    public FactResponse updateFact(UUID id, FactRequest factRequest) {

        FactEntity factEntity = factRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Fact", "id", id.toString())
        );
        factEntity.setTitle(factRequest.getTitle());

        Set<ReferenceEntity> referenceEntities = parseReferenceEntity(factRequest.getUrls());
        factEntity.setReferences(referenceEntities);

        return factMapper.toResponse(factRepository.save(factEntity));
    }

    public void deleteFact(UUID id) {
        factRepository.findById(id).ifPresent(factRepository::delete);
    }

    public List<ReferenceResponse> getFactReferences(UUID id) {

        FactEntity factEntity = factRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Fact", "id", id.toString())
        );
        return factEntity.getReferences().stream().map(referenceMapper::toResponse).toList();
    }

    public List<ReferenceResponse> createFactReferences(UUID id,
                                                        List<ReferenceRequest> referenceRequests) {

        FactEntity factEntity = factRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Fact", "id", id.toString())
        );
        if (factEntity.getReferences() == null) {
            factEntity.setReferences(new HashSet<>());
        }

        List<String> urls = referenceRequests.stream().map(ReferenceRequest::getUrl).toList();
        Set<ReferenceEntity> referenceEntities = factEntity.getReferences();
        referenceEntities.addAll(parseReferenceEntity(urls));
        factEntity.setReferences(referenceEntities);

        factRepository.save(factEntity);

        return factEntity.getReferences().stream().map(referenceMapper::toResponse).toList();
    }

    public void deleteFactReferences(UUID id, UUID referenceId) {
        FactEntity factEntity = factRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Fact", "id", id.toString())
        );

        Set<ReferenceEntity> referenceEntities = factEntity.getReferences();
        referenceEntities.removeIf(referenceEntity -> referenceEntity.getId().equals(referenceId));
        factEntity.setReferences(referenceEntities);

        factRepository.save(factEntity);
    }

    private Set<ReferenceEntity> parseReferenceEntity(List<String> urls) {
        Set<ReferenceEntity> referenceEntities = new HashSet<>();
        List<ReferenceEntity> newReferenceEntities = new ArrayList<>();

        for (String url : urls) {
            referenceRepository.findByUrl(url).ifPresentOrElse(referenceEntities::add,
                    () -> newReferenceEntities.add(new ReferenceEntity(url))
            );
        }

        referenceRepository.saveAll(newReferenceEntities);

        referenceEntities.addAll(newReferenceEntities);
        return referenceEntities;
    }
}
