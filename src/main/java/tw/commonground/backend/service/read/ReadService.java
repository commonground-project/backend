package tw.commonground.backend.service.read;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.event.EventListener;
import tw.commonground.backend.service.follow.FollowService;
import tw.commonground.backend.service.issue.IssueService;
import tw.commonground.backend.service.issue.entity.IssueEntity;
import tw.commonground.backend.service.read.entity.ReadEntity;
import tw.commonground.backend.service.read.entity.ReadKey;
import tw.commonground.backend.service.read.entity.ReadObjectType;
import tw.commonground.backend.service.read.entity.ReadRepository;
import tw.commonground.backend.service.reply.ReplyCreatedEvent;
import tw.commonground.backend.service.reply.ReplyService;
import tw.commonground.backend.service.reply.entity.ReplyEntity;
import tw.commonground.backend.service.user.UserService;
import tw.commonground.backend.service.user.entity.UserEntity;
import tw.commonground.backend.service.viewpoint.ViewpointService;
import tw.commonground.backend.service.viewpoint.entity.ViewpointEntity;
import tw.commonground.backend.shared.event.comment.UserViewpointCommentedEvent;
import tw.commonground.backend.shared.tracing.Traced;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Traced
@Service
public class ReadService {
    private final ReadRepository readRepository;
    private final FollowService followService;
    private final UserService userService;
    private final IssueService issueService;
    private final ViewpointService viewpointService;
    private final ReplyService replyService;

    public ReadService(ReadRepository readRepository, FollowService followService, UserService userService,
                       IssueService issueService, ViewpointService viewpointService, ReplyService replyService) {
        this.readRepository = readRepository;
        this.followService = followService;
        this.userService = userService;
        this.issueService = issueService;
        this.viewpointService = viewpointService;
        this.replyService = replyService;
    }

    public ReadEntity updateReadStatus(Long userId, UUID objectId, ReadObjectType objectType) {
        ReadEntity entity = readRepository.findByIdUserIdAndIdObjectIdAndIdObjectType(userId, objectId, objectType)
                .orElseGet(() -> createReadEntity(userId, objectId, objectType));
        entity.setReadStatus(true);
        return readRepository.save(entity);
    }

    private ReadEntity createReadEntity(Long userId, UUID objectId, ReadObjectType objectType) {
        /*
        * if the read entity does not exist, create a new one
        * the read status is set to false for viewpoint and reply
        * since the new created viewpoint and reply are not read yet
        * the read status is set to true for issue
        * since the new created issue is read
        * */

        ReadEntity entity = new ReadEntity();
        UserEntity user = userService.getUserEntityById(userId);
        entity.setUser(user);

        if (objectType == ReadObjectType.ISSUE) {
            IssueEntity issue = issueService.getIssue(objectId);
            entity.setIssue(issue);
            entity.setReadStatus(true);
        } else if (objectType == ReadObjectType.VIEWPOINT) {
            ViewpointEntity viewpoint = viewpointService.getViewpoint(objectId);
            entity.setViewpoint(viewpoint);
            entity.setReadStatus(false);
        } else if (objectType == ReadObjectType.REPLY) {
            ReplyEntity reply = replyService.getReply(objectId);
            entity.setReply(reply);
            entity.setReadStatus(false);
        } else {
            throw new IllegalArgumentException("Invalid object type for read status update");
        }
        ReadKey key = new ReadKey(userId, objectId, objectType);
        entity.setId(key);
        readRepository.save(entity);
        return entity;
    }

    public Boolean getReadStatus(Long userId, UUID objectId, ReadObjectType objectType) {
        ReadEntity entity = readRepository.findByIdUserIdAndIdObjectIdAndIdObjectType(userId, objectId, objectType)
                .orElseGet(() -> createReadEntity(userId, objectId, objectType));
        return entity.getReadStatus();
    }

    @EventListener
    @Transactional
    public void onReplyCreated(ReplyCreatedEvent event) {
        handleReplyCreatedEvent(event);
    }

    public void handleReplyCreatedEvent(ReplyCreatedEvent event) {
        ReplyEntity reply = event.getReplyEntity();
        ViewpointEntity viewpoint = reply.getViewpoint();
        IssueEntity issue = viewpoint.getIssue();

        // Create or update read status for issue for the followers of the viewpoint
        List<Long> viewpointFollowersId = followService.getViewpointFollowersById(viewpoint.getId());
        for (Long followerId : viewpointFollowersId) {
            unread(followerId, issue.getId(), ReadObjectType.ISSUE);
        }

        // Create or update read status for viewpoint which the reply belongs to for all users
        unreadAllUser(viewpoint.getId(), ReadObjectType.VIEWPOINT);

        // Create or update read status for reply for all users
        // This can be ignored, since new created readEntity of reply is unread
        unreadAllUser(reply.getId(), ReadObjectType.REPLY);
    }

    @EventListener
    @Transactional
    public void onViewpointCreated(UserViewpointCommentedEvent event) {
        handleViewpointCreatedEvent(event);
    }

    public void handleViewpointCreatedEvent(UserViewpointCommentedEvent event) {
        UUID viewpointId = event.getEntityId();
        ViewpointEntity viewpoint = viewpointService.getViewpoint(viewpointId);
        IssueEntity issue = viewpoint.getIssue();

        // Create or update read status for issue which the viewpoint belongs to for all users
        unreadAllUser(issue.getId(), ReadObjectType.ISSUE);

        // Create or update read status for viewpoint for all users
        unreadAllUser(viewpointId, ReadObjectType.VIEWPOINT);
    }


    private void unreadAllUser(UUID objectId, ReadObjectType readObjectType) {
        List<Long> userIds = userService.getUsers().stream().map(UserEntity::getId).toList();
        for (Long userId : userIds) {
            unread(userId, objectId, readObjectType);
        }
    }

    private void unread(Long userId, UUID objectId, ReadObjectType objectType) {
        Optional<ReadEntity> existing = readRepository.findByIdUserIdAndIdObjectIdAndIdObjectType(userId,
                                                                                                  objectId, objectType);
        if (existing.isPresent()) {
            ReadEntity read = existing.get();
            read.setReadStatus(false);
            readRepository.save(read);
            return;
        }

        ReadEntity read = new ReadEntity();
        read.setId(new ReadKey(userId, objectId, objectType));
        UserEntity user = userService.getUserEntityById(userId);
        read.setUser(user);
        read.setReadStatus(false);

        switch (objectType) {
            case ISSUE -> read.setIssue(issueService.getIssue(objectId));
            case VIEWPOINT -> read.setViewpoint(viewpointService.getViewpoint(objectId));
            case REPLY -> read.setReply(replyService.getReply(objectId));
            default -> throw new IllegalArgumentException("Invalid object type for read status update");
        }

        readRepository.save(read);
    }
}
