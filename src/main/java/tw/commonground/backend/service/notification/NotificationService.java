package tw.commonground.backend.service.notification;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import tw.commonground.backend.exception.EntityNotFoundException;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class NotificationService implements ApplicationListener<ReplyCreatedEvent> {

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

    @Override
    public void onApplicationEvent(ReplyCreatedEvent notificationEvent) {
        ReplyEntity replyEntity = notificationEvent.getReplyEntity();
        List<QuoteReply> quotes = notificationEvent.getQuotes();
        UUID viewpointId = replyEntity.getViewpoint().getId();

        ViewpointEntity viewpointEntity = viewpointService.getViewpoint(viewpointId);
        List<FullUserEntity> quoteUsers = new ArrayList<>();
        quotes.forEach(quote -> replyRepository.findById(quote.getReplyId()).ifPresentOrElse(reply -> {
            Long userId = userRepository.getIdByUid(reply.getAuthorId());
            FullUserEntity userEntity = userRepository.findUserEntityById(userId).orElseThrow(
                    () -> new EntityNotFoundException("User", "id", userId.toString())
            );
            if (userSettingService.getUserSetting(userId).getNewReferenceToMyReply()) {
                quoteUsers.add(userEntity);
            }
        }, () -> {
            throw new EntityNotFoundException("Reply", "id", quote.getReplyId().toString());
        }));

        try {
            subscriptionService.sendNotification(quoteUsers,
                    viewpointEntity.getTitle(),
                    "有人節錄了您的回覆");
        } catch (NotificationDeliveryException e) {
            throw new RuntimeException(e);
        }

        Long userId = userRepository.getIdByUid(viewpointEntity.getAuthorId());
        if (userSettingService.getUserSetting(userId).getNewReplyInMyViewpoint()) {
            try {
                subscriptionService.sendNotification(List.of(userRepository.findUserEntityById(userId).orElseThrow(
                        () -> new EntityNotFoundException("User", "id", userId.toString())
                )), viewpointEntity.getTitle(), viewpointEntity.getTitle() + " 下有一則新的回覆！");
            } catch (NotificationDeliveryException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
