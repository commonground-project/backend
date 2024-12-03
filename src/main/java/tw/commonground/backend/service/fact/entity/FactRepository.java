package tw.commonground.backend.service.fact.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface FactRepository extends JpaRepository<FactEntity, UUID> {
    @Query("SELECT f.id FROM FactEntity f WHERE f.id IN :ids")
    List<UUID> findExistingIdsByIds(@Param("ids") List<UUID> ids);
}
