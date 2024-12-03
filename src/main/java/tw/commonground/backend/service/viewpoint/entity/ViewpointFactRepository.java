package tw.commonground.backend.service.viewpoint.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tw.commonground.backend.service.fact.entity.FactEntity;

import java.util.List;
import java.util.UUID;

public interface ViewpointFactRepository extends JpaRepository<ViewpointFactEntity, UUID> {

    @Query("SELECT vf.viewpoint.id AS viewpointId, vf.fact AS fact " +
            "FROM ViewpointFactEntity vf " +
            "WHERE vf.viewpoint.id IN :viewpointIds")
    List<ViewpointFactProjection> findFactsByViewpointIds(@Param("viewpointIds") List<UUID> viewpointIds);

    @Query("SELECT vf.fact FROM ViewpointFactEntity vf WHERE vf.viewpoint.id = :viewpointId")
    List<FactEntity> findFactsByViewpointId(@Param("viewpointId") UUID viewpointId);

    @Query("SELECT vf.viewpoint FROM ViewpointFactEntity vf WHERE vf.fact.id = :factId")
    List<ViewpointEntity> findViewpointsByFactId(@Param("factId") UUID factId);

    @Modifying
    @Query(value = "INSERT INTO viewpoint_fact_entity (viewpoint_id, fact_id) VALUES (?1, ?2)", nativeQuery = true)
    void saveByViewpointIdAndFactId(UUID viewpointId, UUID factId);
}
