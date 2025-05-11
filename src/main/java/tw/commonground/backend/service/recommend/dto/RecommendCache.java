package tw.commonground.backend.service.recommend.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class RecommendCache {
    List<ObjectScore> issues;

    List<ObjectScore> viewpoints;
}
