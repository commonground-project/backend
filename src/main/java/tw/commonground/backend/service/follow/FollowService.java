package tw.commonground.backend.service.follow;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.commonground.backend.exception.EntityNotFoundException;
import tw.commonground.backend.service.follow.entity.FollowEntity;
import tw.commonground.backend.service.follow.entity.FollowKey;
import tw.commonground.backend.service.follow.entity.FollowRepository;
import tw.commonground.backend.service.issue.IssueService;
import tw.commonground.backend.service.user.UserService;
import tw.commonground.backend.service.user.entity.UserRepository;
import tw.commonground.backend.service.viewpoint.ViewpointService;
import tw.commonground.backend.shared.entity.RelatedObject;
import tw.commonground.backend.shared.tracing.Traced;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Traced
@Service
public class FollowService {

    private final FollowRepository followRepository;
    private final UserService userService;
    private final IssueService issueService;
    private final ViewpointService viewpointService;

    public FollowService(FollowRepository followRepository, UserService userService, IssueService issueService, ViewpointService viewpointService) {
        this.followRepository = followRepository;
        this.userService = userService;
        this.issueService = issueService;
        this.viewpointService = viewpointService;
    }

    @Transactional
    public FollowEntity followObject(Long userId, UUID objectId, Boolean follow, RelatedObject objectType) {
        userService.throwIfUserNotExist(userId);
        if (objectType == RelatedObject.ISSUE) {
            issueService.throwIfIssueNotExist(objectId);
        } else {
            viewpointService.throwIfViewpointNotExist(objectId);
        }

        FollowKey id = new FollowKey(userId,objectId, objectType);

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
