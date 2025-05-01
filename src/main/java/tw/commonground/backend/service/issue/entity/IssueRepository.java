package tw.commonground.backend.service.issue.entity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface IssueRepository extends JpaRepository<IssueEntity, UUID> {
    Page<SimpleIssueEntity> findAllIssueEntityBy(Pageable pageable);

    @Query("select count(v) from ViewpointEntity v where v.issue.id = :id")
    Integer getViewpointCount(UUID id);
}
