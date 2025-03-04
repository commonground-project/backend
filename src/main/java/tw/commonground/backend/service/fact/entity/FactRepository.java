package tw.commonground.backend.service.fact.entity;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FactRepository extends JpaRepository<FactEntity, UUID> {

    @Cacheable("fact")
    @Query("SELECT f.id FROM FactEntity f WHERE f.id IN :ids")
    List<UUID> findExistingIdsByIds(@Param("ids") List<UUID> ids);

    @Cacheable("fact")
    Page<FactEntity> findAll(Pageable pageable);

    @Cacheable("fact")
    Optional<FactEntity> findById(UUID id);

    @CacheEvict(value = "fact", allEntries = true)
    FactEntity save(FactEntity factEntity);

    @CacheEvict(value = "fact", allEntries = true)
    void delete(FactEntity factEntity);
}
