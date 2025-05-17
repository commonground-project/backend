package tw.commonground.backend.service.recommend;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tw.commonground.backend.service.viewpoint.entity.ViewpointEntity;
import tw.commonground.backend.service.viewpoint.entity.ViewpointRepository;
import tw.commonground.backend.shared.tracing.Traced;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Traced
@Service
public class RecommendService {

    private final RedisTemplate<String, String> stringRedisTemplate;

    private final ViewpointRepository viewpointRepository;

    public RecommendService(RedisTemplate<String, String> stringRedisTemplate,
                            ViewpointRepository viewpointRepository) {

        this.stringRedisTemplate = stringRedisTemplate;
        this.viewpointRepository = viewpointRepository;
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
            return viewpointRepository.findAll(pageable);
        }
        List<UUID> viewpointIds = recommendViewpointIds.stream().map(UUID::fromString).toList();

        long viewpointCount = viewpointRepository.count();

        List<ViewpointEntity> viewpoints = viewpointRepository.findAllByIds(viewpointIds);

        if (viewpoints.size() != pageable.getPageSize()) {
            int totalRecommendCount = Objects.requireNonNull(stringRedisTemplate.opsForZSet().zCard(key)).intValue();
            viewpoints.addAll(
                    viewpointRepository.findExcludedRecommend(
                            viewpointIds,
                            start + viewpoints.size() - totalRecommendCount,
                            pageable.getPageSize() - viewpoints.size(),
                            LocalDateTime.of(LocalDate.now(), LocalDateTime.MIN.toLocalTime())
                    ));
        }

        return new PageImpl<>(viewpoints, pageable, viewpointCount);
    }
}
