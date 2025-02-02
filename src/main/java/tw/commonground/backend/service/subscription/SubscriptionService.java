package tw.commonground.backend.service.subscription;

import net.minidev.json.JSONObject;
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
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    private final UserRepository userRepository;

    @Value("vapid.public.key")
    private String publicKey;

    @Value("vapid.private.key")
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

    public void removeSubscription(UnsubscriptionRequest request, FullUserEntity user) {

        UserEntity userEntity = userRepository.getUserEntityByUsername(user.getUsername()).orElseThrow(() ->
                new EntityNotFoundException("User not found")
        );
        subscriptionRepository.findByEndpointAndAuthAndP256dhAndUser(
                        request.getEndpoint(), request.getKeys().getAuth(), request.getKeys().getP256dh(), userEntity)
                .ifPresent(subscriptionRepository::delete);
    }

    public void sendNotification(List<FullUserEntity> users, String title, String body)
            throws GeneralSecurityException {
        List<UserEntity> userEntities = userRepository.getUsersByUsername(
                users.stream().map(FullUserEntity::getUsername).toList());

        Security.addProvider(new BouncyCastleProvider());

        PushService pushService = new PushService(
                publicKey,
                privateKey);

        List<SubscriptionEntity> subscriptions = subscriptionRepository.findByUsers(userEntities);

        JSONObject payload = new JSONObject();
        payload.put("title", title);
        payload.put("body", body);

        subscriptions.forEach(subscription -> {
            Subscription sub = new Subscription();
            sub.keys = new Subscription.Keys(subscription.getP256dh(), subscription.getAuth());
            sub.endpoint = subscription.getEndpoint();
            Notification notification = null;
            try {
                notification = new Notification(sub, payload.toString());
            } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException e) {
                throw new RuntimeException(e);
            }
            try {
                pushService.send(notification);
            } catch (GeneralSecurityException | IOException | JoseException | ExecutionException
                     | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
