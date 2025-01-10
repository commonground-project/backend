package tw.commonground.backend.service.reply.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tw.commonground.backend.service.user.entity.BaseEntityWithAuthor;
import tw.commonground.backend.service.viewpoint.entity.ViewpointEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ReplyEntity extends BaseEntityWithAuthor {

    @Id
    @GeneratedValue
    private UUID id;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private String content;

    @ColumnDefault("0")
    private Integer likeCount = 0;

    @ColumnDefault("0")
    private Integer reasonableCount = 0;

    @ColumnDefault("0")
    private Integer dislikeCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    private ViewpointEntity viewpoint;

    @OneToMany(mappedBy = "reply")
    private List<QuoteReplyEntity> quotes;

    @OneToMany(mappedBy = "reply")
    private List<ReplyFactEntity> facts;
}
