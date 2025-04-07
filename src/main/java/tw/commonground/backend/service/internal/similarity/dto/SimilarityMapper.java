package tw.commonground.backend.service.internal.similarity.dto;

import tw.commonground.backend.service.internal.similarity.entity.IssueSimilarityEntity;
import tw.commonground.backend.service.internal.similarity.entity.ViewpointSimilarityEntity;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public final class SimilarityMapper {
    private SimilarityMapper() {
        // Prevent instantiation
    }
    public static SimilarityResponse toResponse(
            UUID userId,
            List<IssueSimilarityEntity> issueSimilarities,
            List<ViewpointSimilarityEntity> viewpointSimilarities) {
        return SimilarityResponse.builder()
                .userId(userId)
                .viewpoints(
                        viewpointSimilarities.stream()
                                .map(vs -> new ViewpointSimilarityResponse(
                                        vs.getViewpoint().getId(), vs.getSimilarity()))
                                .collect(Collectors.toList()))
                .issues(
                        issueSimilarities.stream()
                                .map(is -> new IssueSimilarityResponse(is.getIssue().getId(), is.getSimilarity()))
                                .collect(Collectors.toList()))
                .build();
    }
}
