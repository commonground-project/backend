package tw.commonground.backend.service.follow;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.commonground.backend.service.fact.FactService;
import tw.commonground.backend.service.fact.entity.FactRepository;
import tw.commonground.backend.service.follow.entity.FollowEntity;
import tw.commonground.backend.service.follow.entity.FollowKey;
import tw.commonground.backend.service.follow.entity.FollowRepository;
import tw.commonground.backend.service.issue.entity.IssueRepository;
import tw.commonground.backend.service.issue.entity.ManualFactRepository;
import tw.commonground.backend.shared.entity.RelatedObject;
import tw.commonground.backend.shared.tracing.Traced;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Traced
@Service
public class FollowService {

    private final IssueRepository issueRepository;

    private final FollowRepository followRepository;

    private final ManualFactRepository manualFactRepository;

    private final FactRepository factRepository;

    private final FactService factService;

    public FollowService(IssueRepository issueRepository,
                        FollowRepository followRepository,
                        ManualFactRepository manualFactRepository,
                        FactRepository factRepository,
                        FactService factService) {
        this.issueRepository = issueRepository;
        this.followRepository = followRepository;
        this.manualFactRepository = manualFactRepository;
        this.factRepository = factRepository;
        this.factService = factService;
    }

    @Transactional
    public FollowEntity followObject(Long userId, UUID issueId, Boolean follow, RelatedObject objectType) {
        FollowKey id = new FollowKey(userId, issueId, objectType);
        if (followRepository.findById(id).isPresent()) {
            followRepository.updateFollowById(id, follow);
        } else {
            followRepository.insertFollowById(id, follow);
        }
        FollowEntity followEntity = new FollowEntity();
        followEntity.setId(id);
        followEntity.setFollow(follow);
        followEntity.setUpdatedAt(LocalDateTime.now());
        return followEntity;
    }

    public Boolean getFollow(Long userId, UUID objectId, RelatedObject objectType) {
        FollowKey id = new FollowKey(userId, objectId, objectType);
        return followRepository.findFollowById(id).orElse(false);
    }

    public List<Long> getFollowersById(UUID objectId, RelatedObject objectType) {
        return followRepository.findUsersIdByObjectIdAndFollowTrue(objectId, objectType).orElse(Collections.emptyList());
    }
}
