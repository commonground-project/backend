package tw.commonground.backend.service.subscription;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tw.commonground.backend.service.user.entity.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, UUID> {
    Optional<SubscriptionEntity> findByEndpointAndAuthAndP256dhAndUser(String endpoint,
                                                                       String auth,
                                                                       String p256dh,
                                                                       UserEntity user);

    @Query("select s from SubscriptionEntity s where s.user in :users")
    List<SubscriptionEntity> findByUsers(@Param("users") List<UserEntity> users);


}
