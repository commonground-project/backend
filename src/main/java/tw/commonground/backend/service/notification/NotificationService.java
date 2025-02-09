package tw.commonground.backend.service.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import tw.commonground.backend.service.notification.dto.NotificationDto;
import tw.commonground.backend.service.reply.ReplyCreatedEvent;
import tw.commonground.backend.service.reply.dto.QuoteReply;
import tw.commonground.backend.service.reply.entity.ReplyEntity;
import tw.commonground.backend.service.reply.entity.ReplyRepository;
import tw.commonground.backend.service.subscription.SubscriptionService;
import tw.commonground.backend.service.subscription.exception.NotificationDeliveryException;
import tw.commonground.backend.service.user.UserSettingService;
import tw.commonground.backend.service.user.entity.FullUserEntity;
import tw.commonground.backend.service.user.entity.UserRepository;
import tw.commonground.backend.service.viewpoint.ViewpointService;
import tw.commonground.backend.service.viewpoint.entity.ViewpointEntity;

import java.util.*;

@Slf4j
@Service
public class NotificationService {

    private final ViewpointService viewpointService;

    private final ReplyRepository replyRepository;

    private final UserRepository userRepository;

    private final UserSettingService userSettingService;

    private final SubscriptionService subscriptionService;

    public NotificationService(ViewpointService viewpointService,
                               ReplyRepository replyRepository,
                               UserRepository userRepository,
                               UserSettingService userSettingService,
                               SubscriptionService subscriptionService) {
        this.viewpointService = viewpointService;
        this.replyRepository = replyRepository;
        this.userRepository = userRepository;
        this.userSettingService = userSettingService;
        this.subscriptionService = subscriptionService;
    }

    @EventListener
    public void onReplyCreatedEventCheckViewpointSubscription(ReplyCreatedEvent notificationEvent) {
        // Collect notification data
        ReplyEntity replyEntity = notificationEvent.getReplyEntity();
        Long userId = notificationEvent.getUser().getId();

        // Collect viewpoint data
        UUID viewpointId = replyEntity.getViewpoint().getId();
        ViewpointEntity viewpointEntity = viewpointService.getViewpoint(viewpointId);
        String title = viewpointEntity.getTitle();
        String body = replyEntity.getContent();
        String issueId = viewpointEntity.getIssue().getId().toString();
        String viewpointIdString = viewpointId.toString();

        if (userSettingService.getUserSetting(userId).getNewReplyInMyViewpoint()) {
            NotificationDto dto = NotificationFactory.createViewpointReplyNotification(
                    title, body, issueId, viewpointIdString);

            userRepository.findUserEntityById(userId).ifPresent(user -> {
                try {
                    int sent = sendNotification(user, dto);
                    log.info("Sent viewpoint reply notification to user: {}", user.getUsername());
                } catch (NotificationDeliveryException e) {
                    log.error("Failed to send notification to user: {}", user.getUsername(), e);
                }
            });
        }
    }

    @EventListener
    public void onReplyCreatedEventCheckQuoteSubscription(ReplyCreatedEvent notificationEvent) {
        // Collect notification data
        List<QuoteReply> quotes = notificationEvent.getQuotes();
        ReplyEntity replyEntity = notificationEvent.getReplyEntity();
        Long userId = notificationEvent.getUser().getId();

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
                .flatMap(reply -> userRepository.findUserEntityById(userId)).ifPresent(user -> {

                    // 3. checks if the user has enabled notifications for new references
                    // 4. than adds the user to the needNotificationUser list if true
                    if (userSettingService.getUserSetting(userId).getNewReferenceToMyReply()) {
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

    public int sendNotification(FullUserEntity user, NotificationDto notificationDto) throws
            NotificationDeliveryException {
        return subscriptionService.sendNotification(user,
                notificationDto.getTitle(),
                notificationDto.getBody(),
                notificationDto.getUrl().toString());
    }

    public int sendNotification(List<FullUserEntity> users, NotificationDto notificationDto) throws
            NotificationDeliveryException {
        return subscriptionService.sendNotification(users,
                notificationDto.getTitle(),
                notificationDto.getBody(),
                notificationDto.getUrl().toString());
    }
}
