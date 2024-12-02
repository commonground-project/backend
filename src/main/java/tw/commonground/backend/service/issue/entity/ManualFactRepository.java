package tw.commonground.backend.service.issue.entity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

@SuppressWarnings("MethodName")
public interface ManualFactRepository extends JpaRepository<ManualFactEntity, IssueFactKey> {
    Page<ManualFactEntity> findAllByKey_IssueId(UUID issueId, Pageable pageable);

    Page<ManualFactEntity> findAllByKey_FactId(UUID factId, Pageable pageable);

    @Modifying
    @Query(value = "INSERT INTO manual_fact_entity (issue_id, fact_id) VALUES (?1, ?2)", nativeQuery = true)
    void saveByIssueIdAndFactId(UUID issueId, UUID factId);
}
