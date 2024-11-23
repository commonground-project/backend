package tw.commonground.backend.service.reference;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@EntityListeners(AuditingEntityListener.class)
public class ReferenceEntity {

    @Id
    @GeneratedValue
    private Long id;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createAt;

    @Column(nullable = false, unique = true)
    private String url;

    private String favicon;

    @Column(nullable = false)
    private String title;

}
