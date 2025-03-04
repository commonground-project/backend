package tw.commonground.backend.service.reference;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReferenceRepository extends JpaRepository<ReferenceEntity, Long> {

    @Cacheable("reference")
    Optional<ReferenceEntity> findByUrl(String reference);

    @CacheEvict(value = "reference", allEntries = true)
    ReferenceEntity save(ReferenceEntity referenceEntity);

    @CacheEvict(value = "reference", allEntries = true)
    <S extends ReferenceEntity> List<S> saveAll(Iterable<S> entities);

    @CacheEvict(value = "reference", allEntries = true)
    void delete(ReferenceEntity referenceEntity);
}
