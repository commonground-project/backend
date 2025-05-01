package tw.commonground.backend.service.follow;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.commonground.backend.exception.EntityNotFoundException;
import tw.commonground.backend.service.follow.entity.FollowEntity;
import tw.commonground.backend.service.follow.entity.FollowKey;
import tw.commonground.backend.service.follow.entity.FollowRepository;
import tw.commonground.backend.service.issue.IssueService;
import tw.commonground.backend.service.user.UserService;
import tw.commonground.backend.service.user.entity.UserEntity;
import tw.commonground.backend.service.viewpoint.ViewpointService;
import tw.commonground.backend.shared.entity.RelatedObject;
import tw.commonground.backend.shared.tracing.Traced;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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

//    @Transactional
//    public FollowEntity followObject(Long userId, UUID objectId, Boolean follow, RelatedObject objectType) {
//        // Initialize logger
//        Logger logger = LoggerFactory.getLogger(this.getClass());
//
//        logger.debug("Starting followObject: userId={}, objectId={}, follow={}, objectType={}",
//                userId, objectId, follow, objectType);
//
//        // Validate user existence
//        userService.throwIfUserNotExist(userId);
//        logger.debug("User with ID {} exists", userId);
//
//        // Validate object existence based on type
//        if (objectType == RelatedObject.ISSUE) {
//            issueService.throwIfIssueNotExist(objectId);
//            logger.debug("Issue with ID {} exists", objectId);
//        } else {
//            viewpointService.throwIfViewpointNotExist(objectId);
//            logger.debug("Viewpoint with ID {} exists", objectId);
//        }
//
//        // Load UserEntity for setting in FollowEntity
////        UserEntity user = userService. // Assume userService has a findUserById method
////                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
//
//        userService.throwIfUserNotExist(userId);
//        logger.debug("Loaded UserEntity: id={}", userId);
//
//        UserEntity user = userService.getUserById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
//
//        userService.getUserById(userId)
//                .ifPresentOrElse(
//                        user -> logger.debug("UserEntity found: id={}", user.getId()),
//                        () -> logger.error("UserEntity not found for ID: {}", userId)
//                );
//
//        // Create FollowKey
//        FollowKey id = new FollowKey(userId, objectId, objectType);
//        logger.debug("Created FollowKey: userId={}, objectId={}, objectType={}",
//                id.getUserId(), id.getObjectId(), id.getObjectType());
//
//        UserEntity.
//
//        // Check if FollowEntity exists
//        Optional<FollowEntity> followEntityOptional = followRepository.findById(id);
//        logger.debug("FollowEntity exists: {}", followEntityOptional.isPresent());
//
//        if (followEntityOptional.isPresent()) {
//            FollowEntity followEntity = followEntityOptional.get();
//            logger.debug("Existing FollowEntity: userId={}, objectId={}, current follow={}",
//                    followEntity.getId().getUserId(), followEntity.getId().getObjectId(), followEntity.getFollow());
//
//            followEntity.setFollow(follow);
//            followEntity.setUpdatedAt(LocalDateTime.now());
//            FollowEntity savedEntity = followRepository.save(followEntity);
//            logger.debug("Updated FollowEntity: follow={}", savedEntity.getFollow());
//            return savedEntity;
//        } else {
//            FollowEntity followEntity = new FollowEntity();
//            followEntity.setId(id);
//            followEntity.setUser(user); // FIX: Set the UserEntity
//            followEntity.setFollow(follow);
//            followEntity.setUpdatedAt(LocalDateTime.now());
//
//            // Debug: Validate state before saving
//            if (followEntity.getUser() == null) {
//                logger.error("UserEntity is null in FollowEntity before saving!");
//                throw new IllegalStateException("UserEntity cannot be null in FollowEntity");
//            }
//            logger.debug("New FollowEntity: userId={}, objectId={}, follow={}, userPresent={}",
//                    followEntity.getId().getUserId(), followEntity.getId().getObjectId(),
//                    followEntity.getFollow(), followEntity.getUser() != null);
//
//            FollowEntity savedEntity = followRepository.save(followEntity);
//            logger.debug("Saved new FollowEntity: userId={}, objectId={}, follow={}",
//                    savedEntity.getId().getUserId(), savedEntity.getId().getObjectId(), savedEntity.getFollow());
//            return savedEntity;
//        }
//    }

    @Transactional
    public FollowEntity followObject(Long userId, UUID objectId, Boolean follow, RelatedObject objectType) {
        userService.throwIfUserNotExist(userId);
        if (objectType == RelatedObject.ISSUE) {
            issueService.throwIfIssueNotExist(objectId);
        } else {
            viewpointService.throwIfViewpointNotExist(objectId);
        }
        UserEntity userEntity = userService.getUserById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", "id", userId.toString()));

        FollowKey id = new FollowKey(userId, objectId, objectType);

        Optional<FollowEntity> followEntityOptional = followRepository.findById(id);
        if (followEntityOptional.isPresent()) {
            FollowEntity followEntity = followEntityOptional.get();
            followEntity.setFollow(follow);
            followEntity.setUpdatedAt(LocalDateTime.now());
            return followRepository.save(followEntity);
        } else {
            FollowEntity followEntity = new FollowEntity();
            followEntity.setId(id);
            followEntity.setUser(userEntity);
            followEntity.setFollow(follow);
            followEntity.setUpdatedAt(LocalDateTime.now());
            return followRepository.save(followEntity);
        }
        //            followRepository.updateFollowById(id, follow);
        //            followRepository.insertFollowById(id, follow);
    }

    public Boolean getFollow(Long userId, UUID objectId, RelatedObject objectType) {
        FollowKey id = new FollowKey(userId, objectId, objectType);
        return followRepository.findFollowById(id).orElse(false);
    }

    public List<Long> getFollowersById(UUID objectId, RelatedObject objectType) {
        return followRepository.findUsersIdByObjectIdAndFollowTrue(objectId, objectType).orElse(Collections.emptyList());
    }

    public FollowEntity getfollowObject(Long userId, UUID objectId, RelatedObject objectType) {
        FollowKey key = new FollowKey(userId, objectId, objectType);
        return followRepository.findById(key).orElseThrow(() -> new EntityNotFoundException("Follow", "id", key.toString()));
    }
}
