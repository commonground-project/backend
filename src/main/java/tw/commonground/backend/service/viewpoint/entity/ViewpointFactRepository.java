package tw.commonground.backend.service.viewpoint.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface ViewpointFactRepository extends JpaRepository<ViewpointFactEntity, UUID> {
    @Modifying
    @Query(value = "INSERT INTO viewpoint_fact_entity (viewpoint_id, fact_id) VALUES (?1, ?2)", nativeQuery = true)
    void saveByViewpointIdAndFactId(UUID viewpointId, UUID factId);
}
