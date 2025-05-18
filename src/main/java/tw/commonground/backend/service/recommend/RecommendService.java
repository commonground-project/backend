package tw.commonground.backend.service.recommend;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tw.commonground.backend.service.viewpoint.entity.ViewpointEntity;
import tw.commonground.backend.service.viewpoint.repository.ViewpointRepositoryContainer;
import tw.commonground.backend.shared.tracing.Traced;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Traced
@Service
public class RecommendService {

    private final RedisTemplate<String, String> stringRedisTemplate;

    private final ViewpointRepositoryContainer viewpointRepositoryContainer;

    public RecommendService(RedisTemplate<String, String> stringRedisTemplate,
                            ViewpointRepositoryContainer viewpointRepositoryContainer) {

        this.stringRedisTemplate = stringRedisTemplate;
        this.viewpointRepositoryContainer = viewpointRepositoryContainer;
    }

    public Page<ViewpointEntity> getIssueViewpoints(UUID userId, UUID issueId, Pageable pageable) {
        int start = pageable.getPageNumber() * pageable.getPageSize();
        int end = pageable.getPageNumber() * pageable.getPageSize() + pageable.getPageSize() - 1;

        String key = String.format("recommendation:%s:%s", issueId, userId);

        // Get the recommended viewpoint IDs from Redis
        Set<String> recommendViewpointIds;
        try {
            recommendViewpointIds = stringRedisTemplate.opsForZSet().reverseRange(key, start, end);
        } catch (Exception e) {
            recommendViewpointIds = null;
        }
        if (recommendViewpointIds == null) {
            return viewpointRepositoryContainer.findAllByIssueId(issueId, pageable);
        }
        List<UUID> viewpointIds = recommendViewpointIds.stream().map(UUID::fromString).toList();

        long viewpointCount = viewpointRepositoryContainer.count();

        List<ViewpointEntity> viewpoints = new ArrayList<>(viewpointRepositoryContainer.findAllByIds(viewpointIds));

        if (viewpoints.size() != pageable.getPageSize()) {
            int totalRecommendCount = Objects.requireNonNull(stringRedisTemplate.opsForZSet().zCard(key)).intValue();
            String time = stringRedisTemplate.opsForValue().get("last_updated");
            if (time == null) {
                time = LocalDateTime.of(1, 1, 1, 1, 0, 0).format(DateTimeFormatter.ISO_DATE_TIME);
            }
            LocalDateTime lastUpdated = LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME);

            viewpoints.addAll(viewpointRepositoryContainer.findExcludedRecommend(
                    viewpointIds,
                    issueId,
                    start + viewpoints.size() - totalRecommendCount,
                    pageable.getPageSize() - viewpoints.size(),
                    lastUpdated
            ));
        }

        return new PageImpl<>(viewpoints, pageable, viewpointCount);
    }
}
