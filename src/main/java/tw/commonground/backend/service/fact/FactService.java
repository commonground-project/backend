package tw.commonground.backend.service.fact;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import tw.commonground.backend.service.fact.dao.FactMapper;
import tw.commonground.backend.service.fact.dao.FactRequest;
import tw.commonground.backend.service.fact.dao.FactResponse;
import tw.commonground.backend.service.fact.entity.FactEntity;
import tw.commonground.backend.service.fact.entity.FactRepository;
import tw.commonground.backend.service.reference.ReferenceEntity;
import tw.commonground.backend.service.reference.ReferenceRepository;
import tw.commonground.backend.service.reference.ReferenceRequest;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
public class FactService {

    private final FactRepository factRepository;

    private final ReferenceRepository referenceRepository;

    private final FactMapper mapper = new FactMapper();

    public FactService(FactRepository factRepository, ReferenceRepository referenceRepository) {
        this.factRepository = factRepository;
        this.referenceRepository = referenceRepository;
    }

    public List<FactResponse> getFacts(int page, int size, String column, String mode) {
        Sort sorted = Sort.by((Objects.equals(mode, "asc") ? Sort.Order.by(column) : Sort.Order.desc(column)));
        Pageable pageable = PageRequest.of(page, size, sorted);
        Page<FactEntity> pageFacts = factRepository.findAll(pageable);
        return pageFacts.getContent()
                .stream()
                .map(mapper::toResponse)
                .collect(toList());
    }


    public FactResponse getFact(UUID id) {
        //TODO: Add error handling of entity not found
        return factRepository.findById(id).map(mapper::toResponse).orElse(null);
    }

    public FactResponse createFact(FactRequest factRequest) {
        //TODO: Map to logged in user
        FactEntity factEntity = FactEntity.builder()
                .title(factRequest.getTitle())
                .authorId(1L)
                .authorName("aaa")
                .references(new HashSet<>())
                .build();

        List<String> urls = factRequest.getReferences().stream().map(ReferenceRequest::getUrl).toList();
        addReferenceToFact(factEntity.getReferences(), urls);

        return mapper.toResponse(factRepository.save(factEntity));
    }

    public FactResponse updateFact(UUID id, FactRequest factRequest) {
        //TODO: Add error handling of entity not found
        FactEntity factEntity = factRepository.findById(id).orElse(null);
        assert factEntity != null;

        factEntity.setTitle(factRequest.getTitle());

        List<String> urls = factRequest.getReferences().stream().map(ReferenceRequest::getUrl).toList();

        List<ReferenceEntity> newUrls = factEntity.getReferences().stream()
                .filter(entity -> urls.contains(entity.getUrl())).toList();
        factEntity.setReferences(new HashSet<>(newUrls));

        addReferenceToFact(factEntity.getReferences(), urls);

        return mapper.toResponse(factRepository.save(factEntity));
    }

    public void deleteFact(UUID id) {
        //TODO: Add error handling of entity not found
        factRepository.deleteById(id);
    }

    private void addReferenceToFact(Set<ReferenceEntity> referenceEntities, List<String> urls) {
        Set<String> uniqueUrl = new HashSet<>(urls);

        List<ReferenceEntity> existReference = referenceRepository.findAllByUrlIn(uniqueUrl);
        Map<String, ReferenceEntity> existReferenceMap = existReference.stream()
                .collect(Collectors.toMap(ReferenceEntity::getUrl, Function.identity()));

        for (String url : uniqueUrl) {
            ReferenceEntity existReferenceEntity = existReferenceMap.get(url);
            if (existReferenceEntity == null) {
                existReferenceEntity = ReferenceEntity.builder().url(url).build();
                existReferenceMap.put(url, existReferenceEntity);
            }
            referenceEntities.add(existReferenceEntity);
        }

        referenceRepository.saveAll(referenceEntities);
    }
}
