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

//    @Column(nullable = false)
    private LocalDateTime createdAt;

//    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private String title;

    @Column
    private String content;

//    @Column(nullable = false)
    private UUID authorId;

//    @Column(nullable = false)
    private String authorName;

//    @Column(nullable = false)
    private URI authorAvatar;

    @Embedded
    private ViewpointReaction userReaction = new ViewpointReaction();

//    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer likeCount;

    // Don't use integer default 0 in columnDefinition

//    @Column(nullable = false, columnDefinition = "integer default 0")
    @ColumnDefault("0")
    private Integer reasonableCount;

//    @Column(nullable = false, columnDefinition = "integer default 0")
    @ColumnDefault("0")
    private Integer dislikeCount;

//    @ManyToMany
//    private List<FactEntity> facts;

}
