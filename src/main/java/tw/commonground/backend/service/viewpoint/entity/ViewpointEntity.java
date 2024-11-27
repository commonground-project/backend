package tw.commonground.backend.service.viewpoint.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
//import tw.commonground.backend.service.fact.entity.FactEntity;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
public class ViewpointEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private String title;

    @Column
    private String content;

    private UUID authorId;

    private String authorName;

    private URI authorAvatar;

    @ColumnDefault("0")
    private Integer likeCount;

    // Don't use integer default 0 in columnDefinition

    @ColumnDefault("0")
    private Integer reasonableCount;

    @ColumnDefault("0")
    private Integer dislikeCount;

//    @ManyToMany
//    private List<FactEntity> facts;

}
