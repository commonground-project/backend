package tw.commonground.backend.service.internal.interaction.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InteractionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID userId;

    private UUID objectId;

    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    private InteractionType type;

    @Enumerated(EnumType.STRING)
    private RelatedObjectType objectType;

    @Column(columnDefinition = "TEXT")
    private String content;

}
