package tw.commonground.backend.service.follow.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import tw.commonground.backend.shared.entity.RelatedObject;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@ToString
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class FollowKey implements Serializable {
    private Long userId;
    private UUID objectId;

    @Enumerated(EnumType.STRING)
    private RelatedObject objectType;
}