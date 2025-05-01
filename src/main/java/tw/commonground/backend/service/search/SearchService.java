package tw.commonground.backend.service.search;

import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Index;
import com.meilisearch.sdk.SearchRequest;
import com.meilisearch.sdk.model.SearchResultPaginated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tw.commonground.backend.exception.SearchServiceException;
import tw.commonground.backend.service.reference.ReferenceRepository;
import tw.commonground.backend.shared.tracing.Traced;

import java.util.*;

@Slf4j
@Traced
@Service
public class SearchService {

    private final Client client;
    private final ReferenceRepository referenceRepository;

    @Autowired
    public SearchService(Client client, ReferenceRepository referenceRepository) {
        this.client = client;
        this.referenceRepository = referenceRepository;
    }

    public Page<String> search(String query, Pageable pageable) {
        try {
            Index factEntityIndex = client.index("fact_entity");
            Index referenceEntityIndex = client.index("reference_entity");

            int page = pageable.getPageNumber() + 1;
            int size = pageable.getPageSize();

            SearchRequest factRequest = new SearchRequest(query)
                    .setPage(page)
                    .setHitsPerPage(size);

            SearchRequest referenceRequest = new SearchRequest(query)
                    .setPage(page)
                    .setHitsPerPage(size);

            SearchResultPaginated factEntitySearchResult = (SearchResultPaginated) factEntityIndex.search(factRequest);
            SearchResultPaginated referenceEntitySearchResult =
                    (SearchResultPaginated) referenceEntityIndex.search(referenceRequest);
            List<String> factIds = mergeFactIds(factEntitySearchResult, referenceEntitySearchResult);

            long total = factEntitySearchResult.getTotalHits()
                    + referenceEntitySearchResult.getTotalHits();

            return new PageImpl<>(factIds, pageable, total);
        } catch (Exception e) {
            log.error("Error calling MeiliSearch", e);
            throw new SearchServiceException("Search failed", e);
        }
    }

    private List<String> mergeFactIds(SearchResultPaginated factEntitySearchResult,
                                      SearchResultPaginated referenceEntitySearchResult) {
        List<Map<String, Object>> factHits = new ArrayList<>(factEntitySearchResult.getHits());
        List<Map<String, Object>> referenceHits = new ArrayList<>(referenceEntitySearchResult.getHits());

        Set<String> matchedFactIds = new HashSet<>();

        log.debug("Fact hit count: {}", factHits.size());
        log.debug("Reference hit count: {}", referenceHits.size());

        for (Map<String, Object> factHit : factHits) {
            try {
                String factId = getFactId(factHit);
                matchedFactIds.add(factId);
            } catch (Exception e) {
                log.error("Error processing factHit with id {}: {}", factHit.get("id"), e.getMessage(), e);
            }
        }

        for (Map<String, Object> referenceHit : referenceHits) {
            String referenceId = null;
            try {
                referenceId = referenceHit.get("id").toString();
                UUID referenceUUID = UUID.fromString(referenceId);
                log.info("Processing referenceId: {}", referenceUUID);

                referenceRepository.findById(referenceUUID)
                        .ifPresent(reference -> reference.getFacts()
                                .forEach(fact -> matchedFactIds.add(fact.getId().toString())));
            } catch (Exception e) {
                log.error("Error processing referenceHit with referenceId {}: {}", referenceId, e.getMessage(), e);
            }
        }
        return new ArrayList<>(matchedFactIds);
    }

    private String getFactId(Map<String, Object> factHit) {
        return factHit.get("id").toString();
    }
}