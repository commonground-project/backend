package tw.commonground.backend.service.viewpoint;

import org.springframework.data.jpa.repository.JpaRepository;
import tw.commonground.backend.service.viewpoint.entity.ViewpointEntity;

import java.util.Optional;
import java.util.UUID;

public interface ViewpointRepository extends JpaRepository<ViewpointEntity, UUID> {
    Optional<ViewpointEntity> findViewpointEntityById(UUID viewpointId);
}
