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

    public void removeSubscription(SubscriptionRequest request, FullUserEntity user) {
        SubscriptionEntity subscription = subscriptionRepository.findByEndpoint(request.getEndpoint())
                .filter(sub -> sub.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new EntityNotFoundException("Subscription not found or not owned by user"));

        subscriptionRepository.delete(subscription);
    }
}
