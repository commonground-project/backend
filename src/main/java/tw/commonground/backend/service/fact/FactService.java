package tw.commonground.backend.service.fact;

import jakarta.servlet.http.HttpServletRequest;
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
import tw.commonground.backend.service.reference.*;
import tw.commonground.backend.shared.exceptions.ExceptionResponse;
import tw.commonground.backend.shared.exceptions.IdNotFoundException;
import tw.commonground.backend.shared.pagination.PaginationMapper;
import tw.commonground.backend.shared.pagination.WrappedPaginationResponse;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    public WrappedPaginationResponse<List<FactResponse>> getFacts(int page, int size, String column, String mode) {
        Sort sorted = Sort.by((Objects.equals(mode, "desc") ? Sort.Order.desc(column) : Sort.Order.asc(column)));
        Pageable pageable = PageRequest.of(page, size, sorted);
        Page<FactEntity> pageFacts = factRepository.findAll(pageable);

        List<FactResponse> factResponses = pageFacts.getContent()
                .stream()
                .map(factMapper::toResponse)
                .toList();

        return new WrappedPaginationResponse<>(factResponses, paginationMapper.toResponse(pageFacts));
    }


    public FactResponse getFact(UUID id, HttpServletRequest request) throws ExceptionResponse {

        FactEntity factEntity = factRepository.findById(id).orElseThrow(
                () -> new IdNotFoundException(id, request.getRequestURI())
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

        factRequest.getReferences().ifPresent(references ->
            addReferenceToFact(factEntity.getReferences(), references.stream().map(ReferenceRequest::getUrl).toList())
        );

        return factMapper.toResponse(factRepository.save(factEntity));
    }

    public FactResponse updateFact(UUID id, FactRequest factRequest, HttpServletRequest request)
            throws ExceptionResponse {

        FactEntity factEntity = factRepository.findById(id).orElseThrow(
                () -> new IdNotFoundException(id, request.getRequestURI())
        );

        factEntity.setTitle(factRequest.getTitle());

        factRequest.getReferences().ifPresent(references ->
            updateReferences(references.stream().map(ReferenceRequest::getUrl).toList(), factEntity));

        return factMapper.toResponse(factRepository.save(factEntity));
    }

    public void deleteFact(UUID id, HttpServletRequest request) throws ExceptionResponse {

        FactEntity factEntity = factRepository.findById(id).orElseThrow(
                () -> new IdNotFoundException(id, request.getRequestURI())
        );
        factRepository.delete(factEntity);
    }

    public List<ReferenceResponse> getFactReferences(UUID id, HttpServletRequest request) throws ExceptionResponse {

        FactEntity factEntity = factRepository.findById(id).orElseThrow(
                () -> new IdNotFoundException(id, request.getRequestURI())
        );
        return factEntity.getReferences().stream().map(referenceMapper::toResponse).toList();
    }

    public List<ReferenceResponse> updateFactReferences(UUID id,
                                                        List<ReferenceRequest> referenceRequests,
                                                        HttpServletRequest request) throws ExceptionResponse {

        FactEntity factEntity = factRepository.findById(id).orElseThrow(
                () -> new IdNotFoundException(id, request.getRequestURI())
        );
        if (factEntity.getReferences() == null) {
            factEntity.setReferences(new HashSet<>());
        }

        List<String> urls = referenceRequests.stream().map(ReferenceRequest::getUrl).toList();
        updateReferences(urls, factEntity);

        return factEntity.getReferences().stream().map(referenceMapper::toResponse).toList();
    }

    public void deleteFactReferences(UUID id, long referenceId, HttpServletRequest request) throws ExceptionResponse {

        FactEntity factEntity = factRepository.findById(id).orElseThrow(
                () -> new IdNotFoundException(id, request.getRequestURI())
        );
        Set<ReferenceEntity> referenceEntities = factEntity.getReferences();
        referenceEntities.removeIf(referenceEntity -> referenceEntity.getId().equals(referenceId));
        factEntity.setReferences(referenceEntities);
        factRepository.save(factEntity);
    }

    private void updateReferences(List<String> urls, FactEntity factEntity) {

        List<ReferenceEntity> newUrls = factEntity.getReferences().stream()
                .filter(entity -> urls.contains(entity.getUrl())).toList();
        factEntity.setReferences(new HashSet<>(newUrls));

        addReferenceToFact(factEntity.getReferences(), urls);
    }

    private void addReferenceToFact(Set<ReferenceEntity> referenceEntities, List<String> urls) {

        Set<String> uniqueUrl = new HashSet<>(urls);

        List<ReferenceEntity> existReference = referenceRepository.findAllByUrlIn(uniqueUrl);
        Map<String, ReferenceEntity> existReferenceMap = existReference.stream()
                .collect(Collectors.toMap(ReferenceEntity::getUrl, Function.identity()));

        for (String url : uniqueUrl) {
            ReferenceEntity existReferenceEntity = existReferenceMap.computeIfAbsent(
                    url, k -> ReferenceEntity.builder().url(url).build()
            );
            referenceEntities.add(existReferenceEntity);
        }

        referenceRepository.saveAll(referenceEntities);
    }


}
