package tw.commonground.backend.service.fact;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tw.commonground.backend.exception.EntityNotFoundException;
import tw.commonground.backend.service.fact.dto.FactRequest;
import tw.commonground.backend.service.fact.entity.FactEntity;
import tw.commonground.backend.service.fact.entity.FactRepository;
import tw.commonground.backend.service.reference.*;
import tw.commonground.backend.service.reference.dto.ReferenceRequest;
import tw.commonground.backend.service.user.entity.FullUserEntity;
import tw.commonground.backend.shared.tracing.Traced;

import java.util.*;

@Traced
@Service
@CacheConfig(cacheNames = "fact")
public class FactService {

    private final FactRepository factRepository;

    private final ReferenceService referenceService;

    public FactService(FactRepository factRepository, ReferenceService referenceService) {
        this.factRepository = factRepository;
        this.referenceService = referenceService;
    }

    @Cacheable(key = "#pageable")
    public Page<FactEntity> getFacts(Pageable pageable) {
        return factRepository.findAll(pageable);
    }

    public List<FactEntity> getFacts(List<UUID> ids) {
        return factRepository.findAllByIds(ids);
    }


    @Cacheable(key = "#id")
    public FactEntity getFact(UUID id) {

        return factRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Fact", "id", id.toString())
        );
    }

    @CacheEvict(allEntries = true)
    public FactEntity createFact(FactRequest factRequest, FullUserEntity user) {
        FactEntity factEntity = FactEntity.builder()
                .title(factRequest.getTitle())
                .references(new HashSet<>())
                .build();

        factEntity.setAuthor(user);

        Set<ReferenceEntity> referenceEntities = referenceService.createReferencesFromUrls(factRequest.getUrls());
        factEntity.setReferences(referenceEntities);

        return factRepository.save(factEntity);
    }

    // sameUrls represent the urls which already save in ReferenceEntity
    @CacheEvict(allEntries = true)
    public FactEntity updateFact(UUID id, FactRequest factRequest) {

        FactEntity factEntity = factRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Fact", "id", id.toString())
        );
        factEntity.setTitle(factRequest.getTitle());

        Set<ReferenceEntity> referenceEntities = referenceService.createReferencesFromUrls(factRequest.getUrls());
        factEntity.setReferences(referenceEntities);

        return factRepository.save(factEntity);
    }

    @CacheEvict(allEntries = true)
    public void deleteFact(UUID id) {
        factRepository.findById(id).ifPresent(factRepository::delete);
    }

    @Cacheable(value = "factReference", key = "#id")
    public Set<ReferenceEntity> getFactReferences(UUID id) {

        FactEntity factEntity = factRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Fact", "id", id.toString())
        );
        return factEntity.getReferences();
    }

    @Caching(evict = {
            @CacheEvict(value = "fact", allEntries = true),
            @CacheEvict(value = "factReference", allEntries = true)
    })
    public Set<ReferenceEntity> createFactReferences(UUID id,
                                                     List<ReferenceRequest> referenceRequests) {

        FactEntity factEntity = factRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Fact", "id", id.toString())
        );
        if (factEntity.getReferences() == null) {
            factEntity.setReferences(new HashSet<>());
        }

        List<String> urls = referenceRequests.stream().map(ReferenceRequest::getUrl).toList();
        Set<ReferenceEntity> referenceEntities = factEntity.getReferences();
        referenceEntities.addAll(referenceService.createReferencesFromUrls(urls));
        factEntity.setReferences(referenceEntities);

        factRepository.save(factEntity);

        return factEntity.getReferences();
    }

    @Caching(evict = {
            @CacheEvict(value = "fact", allEntries = true),
            @CacheEvict(value = "factReference", allEntries = true)
    })
    public void deleteFactReferences(UUID id, UUID referenceId) {
        FactEntity factEntity = factRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Fact", "id", id.toString())
        );

        Set<ReferenceEntity> referenceEntities = factEntity.getReferences();
        referenceEntities.removeIf(referenceEntity -> referenceEntity.getId().equals(referenceId));
        factEntity.setReferences(referenceEntities);

        factRepository.save(factEntity);
    }

    public void throwIfFactsNotExist(List<UUID> factIds) {
        List<UUID> existingFactIds = factRepository.findExistingIdsByIds(factIds);
        List<UUID> missingFacts = factIds.stream()
                .filter(factId -> !existingFactIds.contains(factId))
                .toList();
        if (!missingFacts.isEmpty()) {
            throw new EntityNotFoundException("Fact", "ids", missingFacts.toString());
        }
    }
}
