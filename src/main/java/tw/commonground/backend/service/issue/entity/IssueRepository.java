package tw.commonground.backend.service.issue.entity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IssueRepository extends JpaRepository<IssueEntity, UUID> {
    Page<SimpleIssueEntity> findAllIssueEntityBy(Pageable pageable);
}
