package tw.commonground.backend.service.viewpoint.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ViewpointReactionRepository extends JpaRepository<ViewpointReactionEntity, ViewpointReactionKey> {
    @Query("SELECT v.reaction FROM ViewpointReactionEntity v WHERE v.id = :id")
    Optional<Reaction> findReactionById(ViewpointReactionKey id);

    @Modifying
    @Query(value = "INSERT INTO viewpoint_reaction_entity (viewpoint_id, user_id, reaction) " +
            "VALUES (:#{#id.viewpointId}, :#{#id.userId}, :reaction)", nativeQuery = true)
    void insertReaction(ViewpointReactionKey id, String reaction);

    @Modifying
    @Query(value = "UPDATE viewpoint_reaction_entity SET reaction = :reaction " +
            "WHERE viewpoint_id = :#{#id.viewpointId} AND user_id = :#{#id.userId}", nativeQuery = true)
    void updateReaction(ViewpointReactionKey id, String reaction);
}
