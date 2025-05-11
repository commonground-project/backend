package tw.commonground.backend.service.recommend.entity;

import jakarta.persistence.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("recommendations")
public class RecommendEntity {
    @Id
    private String id;

    private String json;
}
