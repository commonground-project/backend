package tw.commonground.backend.service.timeline.entity;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import tw.commonground.backend.service.issue.entity.IssueEntity;

import java.util.List;
import java.util.UUID;

public interface NodeRepository extends JpaRepository<NodeEntity, UUID> {
//    List<NodeEntity> findAllByIssue(IssueEntity issue, Sort sort);

    List<NodeEntity> findAllByIssueId(UUID issueId);
}
