package tw.commonground.backend.service.subscription;

import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tw.commonground.backend.exception.EntityNotFoundException;
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

    @Value("${vapid.public.key}")
    private String publicKey;

    @Value("${vapid.private.key}")
    private String privateKey;

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

    public void removeSubscription(SubscriptionRequest request, FullUserEntity user) {
        SubscriptionEntity subscription = subscriptionRepository.findByEndpoint(request.getEndpoint())
                .filter(sub -> sub.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new EntityNotFoundException("Subscription not found or not owned by user"));

        subscriptionRepository.delete(subscription);
    }

    public void sendNotification(String message)
            throws GeneralSecurityException, JoseException, IOException, ExecutionException, InterruptedException {
        List<SubscriptionEntity> subscriptions = subscriptionRepository.findAll();

        Security.addProvider(new BouncyCastleProvider());

        PushService pushService = new PushService(
                publicKey,
                privateKey);

        for (SubscriptionEntity subscription : subscriptions) {
            Subscription sub = new Subscription();
            sub.keys = new Subscription.Keys(subscription.getP256dh(), subscription.getAuth());
            sub.endpoint = subscription.getEndpoint();
            Notification notification = new Notification(sub, message);
            pushService.send(notification);
        }
    }
}
