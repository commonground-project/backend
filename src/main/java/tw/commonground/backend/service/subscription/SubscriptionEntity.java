package tw.commonground.backend.service.subscription;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;
import tw.commonground.backend.service.user.entity.UserEntity;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private String endpoint;

    private String p256dh;

    private String auth;

    @ManyToOne
    private UserEntity user;

}
