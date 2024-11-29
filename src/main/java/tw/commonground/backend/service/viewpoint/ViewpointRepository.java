package tw.commonground.backend.service.viewpoint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import tw.commonground.backend.service.viewpoint.entity.ViewpointEntity;

import java.util.Optional;
import java.util.UUID;

public interface ViewpointRepository extends JpaRepository<ViewpointEntity, UUID> {
    Optional<ViewpointEntity> findViewpointEntityById(UUID viewpointId);

    // @Modifying
    //@Query("UPDATE ViewpointEntity v SET v.likeCount = v.likeCount - 1 WHERE v.id = :viewpointId AND v.likeCount > 0")
    //void decrementLikeCount(@Param("viewpointId") Long viewpointId);

    @Modifying
    @Query("UPDATE ViewpointEntity v SET v.likeCount = v.likeCount + 1 WHERE v.id = :viewpointId")
    void incrementLikeCount(UUID viewpointId);

    @Modifying
    @Query("UPDATE ViewpointEntity v SET v.likeCount = v.likeCount - 1 WHERE v.id = :viewpointId AND v.likeCount > 0")
    void decrementLikeCount(UUID viewpointId);

    @Modifying
    @Query("UPDATE ViewpointEntity v SET v.dislikeCount = v.dislikeCount + 1 WHERE v.id = :viewpointId")
    void incrementDislikeCount(UUID viewpointId);

    @Modifying
    @Query("UPDATE ViewpointEntity v SET v.dislikeCount = v.dislikeCount - 1 WHERE v.id = :viewpointId AND v.dislikeCount > 0")
    void decrementDislikeCount(UUID viewpointId);

    @Modifying
    @Query("UPDATE ViewpointEntity v SET v.reasonableCount = v.reasonableCount + 1 WHERE v.id = :viewpointId")
    void incrementReasonableCount(UUID viewpointId);

    @Modifying
    @Query("UPDATE ViewpointEntity v SET v.reasonableCount = v.reasonableCount - 1 WHERE v.id = :viewpointId AND v.reasonableCount > 0")
    void decrementReasonableCount(UUID viewpointId);

}
