package tw.commonground.backend.service.recommend;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tw.commonground.backend.service.issue.entity.IssueRepository;
import tw.commonground.backend.service.issue.entity.SimpleIssueEntity;
import tw.commonground.backend.service.recommend.dto.ObjectScore;
import tw.commonground.backend.service.recommend.dto.RecommendCache;
import tw.commonground.backend.service.viewpoint.entity.ViewpointEntity;
import tw.commonground.backend.service.viewpoint.entity.ViewpointRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    public Optional<Page<ViewpointEntity>> getViewpoints(UUID userId, Pageable pageable) {
        String key = "recommendations:" + userId;
        String json = stringRedisTemplate.opsForValue().get(key);
        if (json == null) {
            return Optional.empty();
        }

        try {
            RecommendCache recommendCache = objectMapper.readValue(json, RecommendCache.class);

            List<ObjectScore> viewpoints = recommendCache.getViewpoints();
            int start = pageable.getPageNumber() * pageable.getPageSize();
            int end = Math.min(start + pageable.getPageSize(), viewpoints.size());

            List<UUID> ids = viewpoints.stream()
                    .map(ObjectScore::getObjectId)
                    .toList();

            List<ViewpointEntity> viewpointEntities = viewpointRepository.findAllByIds(ids);

            List<ViewpointEntity> pageViewpoints = viewpointEntities.subList(start, end);

            if (pageViewpoints.isEmpty()) {
                return Optional.empty();
            } else {
                return Optional.of(new PageImpl<>(pageViewpoints, pageable, viewpoints.size()));
            }
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<Page<ViewpointEntity>> getIssueViewpoints(UUID userId, UUID issueId, Pageable pageable) {
        String key = "recommendations:" + userId;
        String json = stringRedisTemplate.opsForValue().get(key);
        if (json == null) {
            return Optional.empty();
        }

        try {
            RecommendCache recommendCache = objectMapper.readValue(json, RecommendCache.class);

            List<ObjectScore> viewpoints = recommendCache.getViewpoints();
            int start = pageable.getPageNumber() * pageable.getPageSize();
            int end = Math.min(start + pageable.getPageSize(), viewpoints.size());

            List<ObjectScore> pageViewpoints = viewpoints.subList(start, end);

            List<UUID> ids = pageViewpoints.stream()
                    .map(ObjectScore::getObjectId)
                    .toList();

            List<ViewpointEntity> viewpointEntities = viewpointRepository.findAllByIdsAndIssueId(ids, issueId);

            if (pageViewpoints.isEmpty()) {
                return Optional.empty();
            } else {
                return Optional.of(new PageImpl<>(viewpointEntities, pageable, viewpoints.size()));
            }
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<Page<SimpleIssueEntity>> getIssues(UUID userId, Pageable pageable) {
        String key = "recommendations:" + userId;
        String json = stringRedisTemplate.opsForValue().get(key);
        if (json == null) {
            return Optional.empty();
        }

        try {
            RecommendCache recommendCache = objectMapper.readValue(json, RecommendCache.class);

            List<ObjectScore> issues = recommendCache.getIssues();
            int start = pageable.getPageNumber() * pageable.getPageSize();
            int end = Math.min(start + pageable.getPageSize(), issues.size());

            var pageIssues = issues.subList(start, end);
            List<UUID> ids = pageIssues.stream()
                    .map(ObjectScore::getObjectId)
                    .toList();
            List<SimpleIssueEntity> issueEntities = issueRepository.findAllByIds(ids);

            return Optional.of(new PageImpl<>(issueEntities, pageable, issues.size()));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

}
