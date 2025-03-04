package tw.commonground.backend.service.issue.entity;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

@SuppressWarnings("MethodName")
public interface ManualFactRepository extends JpaRepository<ManualIssueFactEntity, IssueFactKey> {
    @Cacheable({"fact", "issue"})
    Page<ManualIssueFactEntity> findAllByKey_IssueId(UUID issueId, Pageable pageable);

    Page<ManualIssueFactEntity> findAllByKey_FactId(UUID factId, Pageable pageable);

    @Caching(evict = {
            @CacheEvict(value = "fact", allEntries = true),
            @CacheEvict(value = "issue", allEntries = true)
    })
    @Modifying
    @Query(value = "INSERT INTO issue_fact_entity (issue_id, fact_id) VALUES (?1, ?2)", nativeQuery = true)
    void saveByIssueIdAndFactId(UUID issueId, UUID factId);
}
