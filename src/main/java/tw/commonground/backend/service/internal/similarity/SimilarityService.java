package tw.commonground.backend.service.internal.similarity;

import com.nimbusds.jose.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.commonground.backend.service.internal.similarity.dto.*;
import tw.commonground.backend.service.internal.similarity.entity.*;
import tw.commonground.backend.service.user.entity.UserRepository;
import tw.commonground.backend.shared.tracing.Traced;

import java.util.*;
import java.util.stream.Collectors;

@Traced
@Service
public class SimilarityService {

    private final ViewpointSimilarityRepository viewpointSimilarityRepository;

    private final IssueSimilarityRepository issueSimilarityRepository;

    private final UserRepository userRepository;

    public SimilarityService(ViewpointSimilarityRepository viewpointSimilarityRepository,
                             IssueSimilarityRepository issueSimilarityRepository,
                             UserRepository userRepository) {
        this.viewpointSimilarityRepository = viewpointSimilarityRepository;
        this.issueSimilarityRepository = issueSimilarityRepository;
        this.userRepository = userRepository;
    }

    public Map<UUID, Pair<List<IssueSimilarityEntity>, List<ViewpointSimilarityEntity>>> getSimilarities() {
        Map<UUID, List<IssueSimilarityEntity>> issueSimilarities = getIssueSimilarities();
        Map<UUID, List<ViewpointSimilarityEntity>> viewpointSimilarities = getViewpointSimilarities();

        // Combine the keys from both maps to get a complete set of user IDs
        Set<UUID> userIds = issueSimilarities.keySet();
        userIds.addAll(viewpointSimilarities.keySet());

        return userIds.stream()
                .collect(Collectors.toMap(
                        userId -> userId,
                        userId -> Pair.of(
                                issueSimilarities.getOrDefault(userId, List.of()),
                                viewpointSimilarities.getOrDefault(userId, List.of())
                        )
                ));
    }

    public Pair<List<IssueSimilarityEntity>, List<ViewpointSimilarityEntity>> getSimilaritiesByUserId(UUID id) {
        Long userId = userRepository.getIdByUid(id);
        List<ViewpointSimilarityEntity> viewpointSimilarities = viewpointSimilarityRepository.findByUserId(userId);
        List<IssueSimilarityEntity> issueSimilarities = issueSimilarityRepository.findByUserId(userId);

        return Pair.of(issueSimilarities, viewpointSimilarities);
    }

    @Transactional
    public Map<UUID, Pair<List<IssueSimilarityEntity>, List<ViewpointSimilarityEntity>>> createAllSimilarities(
            List<SimilarityRequest> requests) {

        Map<UUID, Pair<List<IssueSimilarityEntity>, List<ViewpointSimilarityEntity>>> similarities = new HashMap<>();

        for (SimilarityRequest request : requests) {
            Pair<List<IssueSimilarityEntity>, List<ViewpointSimilarityEntity>> similarity =
                    createOrUpdateSimilarity(request.getIssueList(), request.getViewpointList(), request.getUserId());

            similarities.put(request.getUserId(), similarity);
        }

        return similarities;
    }

    @Transactional
    public Pair<List<IssueSimilarityEntity>, List<ViewpointSimilarityEntity>> createOrUpdateSimilarity(
            List<IssueSimilarityRequest> issueSimilarityRequests,
            List<ViewpointSimilarityRequest> viewpointSimilarityRequests,
            UUID id) {

        Long userId = userRepository.getIdByUid(id);

        issueSimilarityRequests.forEach(similarity -> {
            IssueSimilarityKey key = new IssueSimilarityKey(userId, similarity.getIssueId());
            // Check if the similarity already exists, if so, update it, otherwise insert it
            if (issueSimilarityRepository.existsById(key)) {
                issueSimilarityRepository.updateSimilarityById(key, similarity.getSimilarity());
            } else {
                issueSimilarityRepository.insertSimilarityById(key, similarity.getSimilarity());
            }
        });

        viewpointSimilarityRequests.forEach(similarity -> {
            ViewpointSimilarityKey key = new ViewpointSimilarityKey(userId, similarity.getViewpointId());
            // Check if the similarity already exists, if so, update it, otherwise insert it
            if (viewpointSimilarityRepository.existsById(key)) {
                viewpointSimilarityRepository.updateSimilarityById(key, similarity.getSimilarity());
            } else {
                viewpointSimilarityRepository.insertSimilarityById(key, similarity.getSimilarity());
            }
        });

        return getSimilaritiesByUserId(id);
    }

    // This method retrieves all issue similarities from the repository and groups them by user ID.
    private Map<UUID, List<IssueSimilarityEntity>> getIssueSimilarities() {
        List<IssueSimilarityEntity> projections = issueSimilarityRepository.findAll();

        return projections.stream()
                .collect(
                        Collectors.groupingBy(similarities -> similarities.getUser().getUuid())
                );
    }

    // This method retrieves all viewpoint similarities from the repository and groups them by user ID.
    private Map<UUID, List<ViewpointSimilarityEntity>> getViewpointSimilarities() {
        List<ViewpointSimilarityEntity> viewpointSimilarityEntities = viewpointSimilarityRepository.findAll();

        return viewpointSimilarityEntities.stream()
                .collect(
                        Collectors.groupingBy(similarities -> similarities.getUser().getUuid())
                );
    }

}
