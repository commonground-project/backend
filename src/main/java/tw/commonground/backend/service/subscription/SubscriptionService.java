package tw.commonground.backend.service.subscription;

import org.springframework.stereotype.Service;
import tw.commonground.backend.exception.EntityNotFoundException;
import tw.commonground.backend.service.user.entity.FullUserEntity;
import tw.commonground.backend.service.user.entity.UserEntity;
import tw.commonground.backend.service.user.entity.UserRepository;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    private final UserRepository userRepository;

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

        SubscriptionEntity subscription = subscriptionRepository.findByEndpointAndAuthAndP256dhAndUser(
                        request.getEndpoint(), request.getKeys().getAuth(), request.getKeys().getP256dh(), userEntity)
                .orElseThrow(() -> new EntityNotFoundException("Subscription not found"));
        // Question: If not found, should I throw Exception? or just ignore it?

        subscriptionRepository.delete(subscription);
    }
}
