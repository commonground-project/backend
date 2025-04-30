package tw.commonground.backend.service.search;

import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Index;
import com.meilisearch.sdk.model.SearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tw.commonground.backend.service.reference.ReferenceRepository;

import java.util.*;

@Service
public class SearchService {

    private final Client client;
    private final ReferenceRepository referenceRepository;
    private static final Logger logger = LoggerFactory.getLogger(SearchService.class);

    @Autowired
    public SearchService(Client client, ReferenceRepository referenceRepository) {
        this.client = client;
        this.referenceRepository = referenceRepository;
    }

    public List<String> search(String query) throws Exception {
        Index factEntityIndex = client.index("fact_entity");
        Index referenceEntityIndex = client.index("reference_entity");

        SearchResult factEntitySearchResult = factEntityIndex.search(query);
        SearchResult referenceEntitySearchResult = referenceEntityIndex.search(query);

        logger.info("Fact Entity Search Result: {}", factEntitySearchResult.getHits());
        logger.info("Reference Entity Search Result: {}", referenceEntitySearchResult.getHits());

        return mergeFactIds(factEntitySearchResult, referenceEntitySearchResult);
    }

    private List<String> mergeFactIds(SearchResult factEntitySearchResult, SearchResult referenceEntitySearchResult) {
        List<Map<String, Object>> factHits = new ArrayList<>(factEntitySearchResult.getHits());
        List<Map<String, Object>> referenceHits = new ArrayList<>(referenceEntitySearchResult.getHits());

        Set<String> matchedFactIds = new HashSet<>();

        logger.info("Fact Hits: {}", factHits);
        logger.info("Reference Hits: {}", referenceHits);

        for (Map<String, Object> factHit : factHits) {
            try {
                String factId = getFactId(factHit);
                matchedFactIds.add(factId);
            } catch (Exception e) {
                logger.error("Error processing factHit: {}", factHit, e);
            }
        }

        for (Map<String, Object> referenceHit : referenceHits) {
            String referenceId = null;
            try {
                referenceId = referenceHit.get("id").toString();
                UUID referenceUUID = UUID.fromString(referenceId);
                logger.info("Processing referenceId: {}", referenceUUID);

                referenceRepository.findById(referenceUUID)
                        .ifPresent(reference -> reference.getFacts()
                                .forEach(fact -> matchedFactIds.add(fact.getId().toString())));
            } catch (IllegalArgumentException e) {
                logger.error("Invalid UUID format for referenceId: {}", referenceId, e);
            } catch (Exception e) {
                logger.error("Error processing referenceHit with referenceId: {}", referenceId, e);
            }
        }
        return new ArrayList<>(matchedFactIds);
    }

    private String getFactId(Map<String, Object> factHit) {
        return factHit.get("id").toString();
    }
}