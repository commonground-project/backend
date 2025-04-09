package tw.commonground.backend.service.issue.entity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface IssueRepository extends JpaRepository<IssueEntity, UUID> {
    Page<SimpleIssueEntity> findAllIssueEntityBy(Pageable pageable);

    @Query("SELECT i FROM IssueEntity i "
            + "LEFT JOIN IssueSimilarityEntity is ON i.id = is.key.issueId AND is.key.userId = :userId "
            + "ORDER BY "
            + "CASE WHEN is.similarity IS NOT NULL THEN 0 ELSE 1 END, "
            + "is.similarity DESC, "
            + "i.createdAt DESC")
    Page<SimpleIssueEntity> findAllWithSimilarity(Long userId, Pageable pageable);
}
