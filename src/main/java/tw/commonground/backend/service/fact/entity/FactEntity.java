package tw.commonground.backend.service.fact.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tw.commonground.backend.service.reference.ReferenceEntity;
import tw.commonground.backend.service.user.entity.BaseEntityWithAuthor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class FactEntity extends BaseEntityWithAuthor {

    @Id
    @GeneratedValue
    private UUID id;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    @Column(nullable = false)
    private String title;

    @ManyToMany
    @JoinTable(
            name = "fact_entity_reference",
            joinColumns = @JoinColumn(name = "fact_entity_id"),
            inverseJoinColumns = @JoinColumn(name = "references_id")
    )
    @ToString.Exclude
    private Set<ReferenceEntity> references;
}
