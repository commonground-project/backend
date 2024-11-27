package tw.commonground.backend.service.viewpoint;

import org.springframework.data.jpa.repository.JpaRepository;
import tw.commonground.backend.service.viewpoint.entity.Reaction;
import tw.commonground.backend.service.viewpoint.entity.ViewpointReactionEntity;
import tw.commonground.backend.service.viewpoint.entity.ViewpointReactionId;

import java.util.Optional;

public interface ViewpointReactionRepository extends JpaRepository<ViewpointReactionEntity, ViewpointReactionId> {
    Optional<ViewpointReactionEntity> findById(ViewpointReactionId id);

    // need to query who liked the viewpoint



}