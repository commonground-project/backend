package tw.commonground.backend.service.timeline.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NodeRepository extends JpaRepository<NodeEntity, UUID> {
    List<NodeEntity> findAllByIssueId(UUID issueId);
}
