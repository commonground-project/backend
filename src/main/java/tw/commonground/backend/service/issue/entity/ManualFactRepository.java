package tw.commonground.backend.service.issue.entity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ManualFactRepository extends JpaRepository<ManualFactEntity, IssueFactKey> {
    Page<ManualFactEntity> findAllByIssueId(UUID issueId, Pageable pageable);

    Page<ManualFactEntity> findAllByFactId(UUID factId, Pageable pageable);
}
