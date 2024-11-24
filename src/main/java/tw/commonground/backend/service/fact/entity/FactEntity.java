package tw.commonground.backend.service.fact.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tw.commonground.backend.service.reference.ReferenceEntity;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "Fact")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@EntityListeners(AuditingEntityListener.class)
public class FactEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createAt;

    @LastModifiedDate
    @Column
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime deletedAt;

    @Column(nullable = false)
    private String title;

    // TODO: Waiting User Entity
    @Column(nullable = false)
    private Long authorId;

    @Column(nullable = false)
    private String authorName;

    @Column(nullable = false)
    private String authorAvatar;

    @ManyToMany
    private Set<ReferenceEntity> references;
}
