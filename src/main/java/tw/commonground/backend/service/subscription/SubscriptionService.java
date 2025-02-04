package tw.commonground.backend.service.subscription;

import jakarta.annotation.PostConstruct;
import net.minidev.json.JSONObject;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tw.commonground.backend.exception.EntityNotFoundException;
import tw.commonground.backend.service.subscription.exception.NotificationDeliveryException;
import tw.commonground.backend.service.user.entity.FullUserEntity;
import tw.commonground.backend.service.user.entity.UserEntity;
import tw.commonground.backend.service.user.entity.UserRepository;

import java.io.IOException;
import java.security.GeneralSecurityException;

import java.security.Security;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    private final UserRepository userRepository;

    private PushService pushService;

    @Value("vapid.public.key")
    private String publicKey;

    @Value("vapid.private.key")
    private String privateKey;

    @PostConstruct
    private void init() throws GeneralSecurityException {
        pushService = new PushService(publicKey, privateKey);
    }

    public SubscriptionService(SubscriptionRepository subscriptionRepository,
                               UserRepository userRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
    }

    public void saveSubscription(SubscriptionRequest request, FullUserEntity user) {
        UserEntity userEntity = userRepository.getUserEntityByUsername(user.getUsername()).orElseThrow(() ->
                new EntityNotFoundException("User not found")
        );
        subscriptionRepository.save(SubscriptionEntity.builder()
                .endpoint(request.getEndpoint())
                .p256dh(request.getKeys().getP256dh())
                .auth(request.getKeys().getAuth())
                .user(userEntity)
                .build());
    }

    public void removeSubscription(UnsubscriptionRequest request, FullUserEntity user) {

        UserEntity userEntity = userRepository.getUserEntityByUsername(user.getUsername()).orElseThrow(() ->
                new EntityNotFoundException("User not found")
        );
        subscriptionRepository.findByEndpointAndAuthAndP256dhAndUser(
                        request.getEndpoint(), request.getKeys().getAuth(), request.getKeys().getP256dh(), userEntity)
                .ifPresent(subscriptionRepository::delete);
    }

    public int sendNotification(List<FullUserEntity> users, String title, String body)
            throws NotificationDeliveryException {
        List<UserEntity> userEntities = userRepository.getUsersByUsername(
                users.stream().map(FullUserEntity::getUsername).toList());

        Security.addProvider(new BouncyCastleProvider());

        List<SubscriptionEntity> subscriptions = subscriptionRepository.findByUsers(userEntities);

        JSONObject payload = new JSONObject();
        payload.put("title", title);
        payload.put("body", body);

        StringBuilder stringbuilder = new StringBuilder();
        for (SubscriptionEntity subscription : subscriptions) {
            try {
                Subscription sub = new Subscription();
                sub.keys = new Subscription.Keys(subscription.getP256dh(), subscription.getAuth());
                sub.endpoint = subscription.getEndpoint();
                Notification notification = new Notification(sub, payload.toString());
                pushService.send(notification);
            } catch (GeneralSecurityException
                     | JoseException
                     | IOException
                     | ExecutionException
                     | InterruptedException e) {
                stringbuilder.append(e.getMessage());
            }
        }

        if (stringbuilder.isEmpty()) {
            return subscriptions.size();
        } else {
            throw new NotificationDeliveryException(stringbuilder.toString());
        }
    }
}
