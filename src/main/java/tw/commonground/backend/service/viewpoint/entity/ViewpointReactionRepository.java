package tw.commonground.backend.service.viewpoint.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ViewpointReactionRepository extends JpaRepository<ViewpointReactionEntity, ViewpointReactionId> {
    Optional<ViewpointReactionEntity> findById(ViewpointReactionId id);

    @Query("SELECT v.reaction FROM ViewpointReactionEntity v WHERE v.id = :id")
    Optional<Reaction> findReactionById(ViewpointReactionId id);

    // need to query who liked the viewpoint



}