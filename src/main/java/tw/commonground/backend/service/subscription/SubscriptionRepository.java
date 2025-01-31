package tw.commonground.backend.service.subscription;

import org.springframework.data.jpa.repository.JpaRepository;
import tw.commonground.backend.service.user.entity.UserEntity;

import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, UUID> {
    Optional<SubscriptionEntity> findByEndpointAndAuthAndP256dhAndUser(String endpoint, String auth, String p256dh, UserEntity user);
}
