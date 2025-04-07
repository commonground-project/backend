package tw.commonground.backend.service.internal.similarity;

import com.nimbusds.jose.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tw.commonground.backend.service.internal.similarity.dto.SimilarityMapper;
import tw.commonground.backend.service.internal.similarity.dto.SimilarityRequest;
import tw.commonground.backend.service.internal.similarity.dto.SimilarityResponse;
import tw.commonground.backend.service.internal.similarity.dto.UserSimilarityRequest;
import tw.commonground.backend.service.internal.similarity.entity.IssueSimilarityEntity;
import tw.commonground.backend.service.internal.similarity.entity.ViewpointSimilarityEntity;
import tw.commonground.backend.shared.tracing.Traced;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Traced
@RestController
@RequestMapping("/api/internal")
public class SimilarityController {

    private final SimilarityService similarityService;

    public SimilarityController(SimilarityService similarityService) {
        this.similarityService = similarityService;
    }

    @GetMapping("/similarities")
    public ResponseEntity<List<SimilarityResponse>> getSimilarities() {
        Map<UUID, Pair<List<IssueSimilarityEntity>, List<ViewpointSimilarityEntity>>> similarities
                = similarityService.getSimilarities();

        return ResponseEntity.ok(similarities.keySet().stream()
                .map(userId -> {
                    Pair<List<IssueSimilarityEntity>, List<ViewpointSimilarityEntity>> pair
                            = similarities.get(userId);
                    return SimilarityMapper.toResponse(userId, pair.getLeft(), pair.getRight());
                }).toList());
    }

    @PostMapping("/similarities")
    public ResponseEntity<List<SimilarityResponse>> createSimilarities(@RequestBody List<SimilarityRequest> requests) {
        Map<UUID, Pair<List<IssueSimilarityEntity>, List<ViewpointSimilarityEntity>>> similarities =
                similarityService.createAllSimilarities(requests);

        return ResponseEntity.ok(similarities.keySet().stream()
                .map(userId -> {
                    Pair<List<IssueSimilarityEntity>, List<ViewpointSimilarityEntity>> pair
                            = similarities.get(userId);
                    return SimilarityMapper.toResponse(userId, pair.getLeft(), pair.getRight());
                }).toList());
    }

    @GetMapping("/similarities/{userId}")
    public ResponseEntity<SimilarityResponse> getSimilarityByUserId(@PathVariable("userId") UUID userId) {
        Pair<List<IssueSimilarityEntity>, List<ViewpointSimilarityEntity>> pair
                = similarityService.getSimilaritiesByUserId(userId);

        SimilarityResponse response = SimilarityMapper.toResponse(userId, pair.getLeft(), pair.getRight());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/similarities/{userId}")
    public ResponseEntity<SimilarityResponse> updateSimilarityByUserId(
            @PathVariable("userId") UUID userId,
            @RequestBody UserSimilarityRequest request) {

        Pair<List<IssueSimilarityEntity>, List<ViewpointSimilarityEntity>> pair
                = similarityService.createOrUpdateSimilarity(
                        request.getIssueList(), request.getViewpointList(), userId);

        SimilarityResponse response = SimilarityMapper.toResponse(userId, pair.getLeft(), pair.getRight());
        return ResponseEntity.ok(response);
    }
}
