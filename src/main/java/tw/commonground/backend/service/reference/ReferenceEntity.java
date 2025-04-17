package tw.commonground.backend.service.reference;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
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

    @Column(nullable = false, unique = true, columnDefinition = "TEXT")
    private String url;

    private String favicon;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

}
