package tw.commonground.backend.service.viewpoint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import tw.commonground.backend.service.viewpoint.entity.Reaction;
import tw.commonground.backend.service.viewpoint.entity.ViewpointReactionEntity;
import tw.commonground.backend.service.viewpoint.entity.ViewpointReactionId;

import java.util.Optional;

public interface ViewpointReactionRepository extends JpaRepository<ViewpointReactionEntity, ViewpointReactionId> {
    Optional<ViewpointReactionEntity> findById(ViewpointReactionId id);

    // @Modifying
    //@Query("UPDATE ViewpointEntity v SET v.likeCount = v.likeCount - 1 WHERE v.id = :viewpointId AND v.likeCount > 0")
    //void decrementLikeCount(@Param("viewpointId") Long viewpointId);

    // need to query who liked the viewpoint



}