package tw.commonground.backend.service.reference;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tw.commonground.backend.service.fact.entity.FactEntity;

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
public class ReferenceEntity {

    public ReferenceEntity(String url) {
        this.url = url;
    }

    @Id
    @GeneratedValue
    private UUID id;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createAt;

    @Column(nullable = false, unique = true)
    private String url;

    private String favicon;

    private String title;

    @ManyToMany(mappedBy = "references")
    @ToString.Exclude
    @JsonIgnore
    private Set<FactEntity> facts;
}
