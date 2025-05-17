package tw.commonground.backend.service.issue.entity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

@SuppressWarnings("MethodName")
public interface ManualFactRepository extends JpaRepository<ManualIssueFactEntity, IssueFactKey> {
    Page<ManualIssueFactEntity> findAllByKey_IssueId(UUID issueId, Pageable pageable);

    Page<ManualIssueFactEntity> findAllByKey_FactId(UUID factId, Pageable pageable);

    @Modifying
    @Query(value = "INSERT INTO issue_fact_entity (issue_id, fact_id, created_at, updated_at) "
            + "VALUES (?1, ?2, now(), now())", nativeQuery = true)
    void saveByIssueIdAndFactId(UUID issueId, UUID factId);
}
