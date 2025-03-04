package tw.commonground.backend.service.subscription;

import jakarta.persistence.*;
import lombok.*;
import tw.commonground.backend.service.user.entity.UserEntity;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(columnDefinition = "TEXT")
    private String endpoint;

    private String p256dh;

    private String auth;

    @ManyToOne
    private UserEntity user;

}
