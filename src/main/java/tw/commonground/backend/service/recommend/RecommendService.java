package tw.commonground.backend.service.recommend;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tw.commonground.backend.service.issue.entity.IssueRepository;
import tw.commonground.backend.service.viewpoint.entity.ViewpointEntity;
import tw.commonground.backend.service.viewpoint.entity.ViewpointRepository;

import java.util.*;

@Service
public class RecommendService {

    private final RedisTemplate<String, String> stringRedisTemplate;

    private final ViewpointRepository viewpointRepository;

    private final IssueRepository issueRepository;

    private final ObjectMapper objectMapper;

    public RecommendService(RedisTemplate<String, String> stringRedisTemplate,
                            ViewpointRepository viewpointRepository,
                            IssueRepository issueRepository,
                            ObjectMapper objectMapper) {

        this.stringRedisTemplate = stringRedisTemplate;
        this.viewpointRepository = viewpointRepository;
        this.issueRepository = issueRepository;
        this.objectMapper = objectMapper;
    }

    public List<ViewpointEntity> getIssueViewpoints(UUID userId, UUID issueId, Pageable pageable) {
        int start = pageable.getPageNumber() * pageable.getPageSize();
        int end = pageable.getPageNumber() * pageable.getPageSize() + pageable.getPageSize() - 1;

        String key = String.format("recommendation:%s:%s", issueId, userId);

        Set<String> recommendViewpointIds = stringRedisTemplate.opsForZSet().reverseRange(key, start, end);
        if (recommendViewpointIds == null || recommendViewpointIds.isEmpty()) {
            return List.of();
        }

        List<UUID> viewpointIds = recommendViewpointIds.stream().map(UUID::fromString).toList();

        List<ViewpointEntity> viewpoints = viewpointRepository.findAllByIds(viewpointIds);

        if (viewpoints.size() != pageable.getPageSize()) {
            viewpoints.addAll(
                    viewpointRepository
                            .findExcludedBySize(viewpointIds, pageable.getPageSize() - viewpoints.size()
                            ));
        }

        return viewpoints;
    }

    public int getIssueViewpointsCount(UUID userId, UUID issueId) {
        String key = String.format("recommendation:%s:%s", issueId, userId);
        Long size = stringRedisTemplate.opsForZSet().size(key);
        if (size == null) {
            return 0;
        }
        return size.intValue();
    }

//    public List<String> getIssues(UUID userId, Pageable pageable) {
//        int start = pageable.getPageNumber() * pageable.getPageSize();
//        int end = pageable.getPageNumber() * pageable.getPageSize() + pageable.getPageSize() - 1;
//
//        String key = String.format("recommendation:%s", userId);
//
//        return stringRedisTemplate.opsForZSet().reverseRange(key, start, end).stream().toList();
//    }
//
//    public int getIssuesCount(UUID userId) {
//        String key = String.format("recommendation:%s", userId);
//        Long size = stringRedisTemplate.opsForZSet().size(key);
//        if (size == null) {
//            return 0;
//        }
//        return size.intValue();
//    }



}
