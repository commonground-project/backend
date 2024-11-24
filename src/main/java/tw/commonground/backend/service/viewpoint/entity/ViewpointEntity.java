package tw.commonground.backend.service.viewpoint.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tw.commonground.backend.service.fact.entity.FactEntity;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ViewpointEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private String title;

    @Column
    private String content;

    @Column(nullable = false)
    private UUID authorId;

    @Column(nullable = false)
    private String authorName;

    @Column(nullable = false)
    private URI authorAvatar;

    @Column(nullable = false)
    private ViewPointReaction userReaction;

    @Column(nullable = false)
    private Integer likeCount;

    @Column(nullable = false)
    private Integer reasonableCount;

    @Column(nullable = false)
    private Integer dislikeCount;

    @ManyToMany
    private List<FactEntity> facts;

}
