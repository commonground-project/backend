package tw.commonground.backend.service.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import tw.commonground.backend.service.follow.FollowService;
import tw.commonground.backend.service.issue.entity.IssueEntity;
import tw.commonground.backend.service.notification.dto.NotificationDto;
import tw.commonground.backend.service.reply.ReplyCreatedEvent;
import tw.commonground.backend.service.reply.dto.QuoteReply;
import tw.commonground.backend.service.reply.entity.ReplyEntity;
import tw.commonground.backend.service.reply.entity.ReplyRepository;
import tw.commonground.backend.service.subscription.SubscriptionService;
import tw.commonground.backend.service.subscription.exception.NotificationDeliveryException;
import tw.commonground.backend.service.timeline.NodeCreatedEvent;
import tw.commonground.backend.service.user.UserSettingService;
import tw.commonground.backend.service.user.entity.FullUserEntity;
import tw.commonground.backend.service.user.entity.UserRepository;
import tw.commonground.backend.service.viewpoint.ViewpointCreatedEvent;
import tw.commonground.backend.service.viewpoint.ViewpointService;
import tw.commonground.backend.service.viewpoint.entity.ViewpointEntity;
import tw.commonground.backend.shared.tracing.Traced;

import java.util.*;

@Slf4j
@Traced
@Service
public class NotificationService {

    private final ViewpointService viewpointService;

    private final FollowService followService;

    private final ReplyRepository replyRepository;

    private final UserRepository userRepository;

    private final UserSettingService userSettingService;

    private final SubscriptionService subscriptionService;

    public NotificationService(ViewpointService viewpointService,
                               ReplyRepository replyRepository,
                               UserRepository userRepository,
                               UserSettingService userSettingService,
                               FollowService followService,
                               SubscriptionService subscriptionService) {
        this.viewpointService = viewpointService;
        this.replyRepository = replyRepository;
        this.userRepository = userRepository;
        this.userSettingService = userSettingService;
        this.subscriptionService = subscriptionService;
        this.followService = followService;
    }

    @EventListener
    public void onViewpointCreatedEventCheckIssueSubscription(ViewpointCreatedEvent notificationEvent) {
        // Collect notification data
        ViewpointEntity viewpointEntity = notificationEvent.getViewpointEntity();

        // Collect issue data
        String viewpointTitle = viewpointEntity.getTitle();
        IssueEntity issue = viewpointEntity.getIssue();
        String issueTitle = issue.getTitle();
        String issueId = issue.getId().toString();
        String viewpointId = viewpointEntity.getId().toString();

        // Collect the issue's followers
        List<Long> followerIds = followService.getIssueFollowersById(issue.getId());
        List<FullUserEntity> needNotificationUsers = new ArrayList<>();

        followerIds.forEach(userId ->
                userRepository.findUserEntityById(userId).ifPresent(user -> {
                    if (userSettingService.getUserSetting(userId).getNewEventInFollowedIssue()) {
                        needNotificationUsers.add(user);
                    }
                })
        );

        if (!needNotificationUsers.isEmpty()) {
            try {
                NotificationDto dto = NotificationFactory.createIssueViewpointNotification(
                        issueTitle, viewpointTitle, issueId, viewpointId);
                int sent = sendNotification(needNotificationUsers, dto);
                log.info("Sent new viewpoint notification to {} issue followers", sent);
            } catch (NotificationDeliveryException e) {
                log.error("Failed to send notification to users: {}", needNotificationUsers, e);
            }
        }
    }

    @EventListener
    public void onReplyCreatedEventCheckViewpointSubscription(ReplyCreatedEvent notificationEvent) {
        // Collect notification data
        ReplyEntity replyEntity = notificationEvent.getReplyEntity();

        // Collect viewpoint data
        UUID viewpointId = replyEntity.getViewpoint().getId();
        ViewpointEntity viewpointEntity = viewpointService.getViewpoint(viewpointId);
        UUID authorId = viewpointEntity.getAuthorId();
        Long authorUserId = userRepository.getIdByUid(authorId);
        String title = viewpointEntity.getTitle();
        String body = replyEntity.getContent();
        String issueId = viewpointEntity.getIssue().getId().toString();
        String viewpointIdString = viewpointId.toString();

        Set<FullUserEntity> needNotificationUsers = new HashSet<>();

        // 1. Notify the viewpoint author if they have `newReplyInMyViewpoint` enabled
        if (userSettingService.getUserSetting(authorUserId).getNewReplyInMyViewpoint()) {
            // TODO: add check if the author follows or unfollows the viewpoint
            userRepository.findUserEntityById(authorUserId).ifPresent(needNotificationUsers::add);
        }

        // 2. Notify users who follow this viewpoint and have `newReplyInFollowedViewpoint` enabled
        List<Long> viewpointFollowerIds = followService.getViewpointFollowersById(viewpointEntity.getId());
        viewpointFollowerIds.forEach(userId ->
                userRepository.findUserEntityById(userId).ifPresent(user -> {
                    if (userSettingService.getUserSetting(userId).getNewReplyInFollowedViewpoint()) {
                        needNotificationUsers.add(user);
                    }
                })
        );

        log.info("Need notification users: {}", needNotificationUsers);

        if (!needNotificationUsers.isEmpty()) {
            try {
                NotificationDto dto = NotificationFactory.createViewpointReplyNotification(
                        title, body, issueId, viewpointIdString);
                int sent = sendNotification(new ArrayList<>(needNotificationUsers), dto);
                log.info("Sent viewpoint reply notification to {} users", sent);
            } catch (NotificationDeliveryException e) {
                log.error("Failed to send notification to users: {}", needNotificationUsers, e);
            }
        }
    }

    @EventListener
    public void onReplyCreatedEventCheckQuoteSubscription(ReplyCreatedEvent notificationEvent) {
        // Collect notification data
        List<QuoteReply> quotes = notificationEvent.getQuotes();
        ReplyEntity replyEntity = notificationEvent.getReplyEntity();
        FullUserEntity user = notificationEvent.getUser();

        // Collect viewpoint data
        UUID viewpointId = replyEntity.getViewpoint().getId();
        ViewpointEntity viewpointEntity = viewpointService.getViewpoint(viewpointId);
        String body = replyEntity.getContent();
        String issueId = viewpointEntity.getIssue().getId().toString();
        String viewpointIdString = viewpointId.toString();

        // Collect users that need to be notified
        List<FullUserEntity> needNotificationUser = new ArrayList<>();

        // 1. iterates over quotes
        // 2. finds the associated ReplyEntity and FullUserEntity
        quotes.forEach(quote -> replyRepository.findById(quote.getReplyId())
                // flatMap only executes the lambda if the optional is present
                .flatMap(reply -> userRepository.findUserEntityById(user.getId())).ifPresent(u -> {

                    // 3. checks if the user has enabled notifications for new references
                    // 4. than adds the user to the needNotificationUser list if true
                    if (userSettingService.getUserSetting(user.getId()).getNewReferenceToMyReply()) {
                        needNotificationUser.add(user);
                    }
                }));

        try {
            NotificationDto dto = NotificationFactory.createQuoteReplyNotification(
                    body, issueId, viewpointIdString);

            int sent = sendNotification(needNotificationUser, dto);
            log.info("Sent quote reply notification to {} users", sent);
        } catch (NotificationDeliveryException e) {
            log.error("Failed to send notification to users: {}", needNotificationUser, e);
        }
    }

    @EventListener
    public void onNodeCreatedEventCheckIssueSubscription(NodeCreatedEvent nodeCreatedEvent) {
        // Collect notification data
        IssueEntity issueEntity = nodeCreatedEvent.getIssueEntity();

        // Collect issue data
        String title = issueEntity.getTitle();
        String body = nodeCreatedEvent.getNodeEntity().getDescription();
        String issueId = issueEntity.getId().toString();

        // Collect users that need to be notified
        List<Long> userIds = followService.getIssueFollowersById(issueEntity.getId());
        List<FullUserEntity> needNotificationUser = new ArrayList<>();

        userIds.forEach(userId -> userRepository.findUserEntityById(userId).ifPresent(user -> {
            if (userSettingService.getUserSetting(userId).getNewNodeOfTimelineToFollowedIssue()) {
                needNotificationUser.add(user);
            }
        }));

        try {
            NotificationDto dto = NotificationFactory.createNodeOfFollowedIssueNotification(
                    title, body, issueId);
            int sent = sendNotification(needNotificationUser, dto);
            log.info("Sent node of followed issue notification to {} users", sent);
        } catch (NotificationDeliveryException e) {
            log.error("Failed to send notification to users: {}", needNotificationUser, e);
        }
    }

    public int sendNotification(List<FullUserEntity> users, NotificationDto notificationDto) throws
            NotificationDeliveryException {
        return subscriptionService.sendNotification(users,
                notificationDto.getTitle(),
                notificationDto.getBody(),
                notificationDto.getUrl());
    }
}