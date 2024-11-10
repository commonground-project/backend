package tw.commonground.backend.service.reference;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Reference")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@EntityListeners(AuditingEntityListener.class)
public class ReferenceEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @CreatedBy
    @Column(nullable = false)
    private LocalDateTime createAt;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String favicon;

    @Column(nullable = false)
    private String title;

}
