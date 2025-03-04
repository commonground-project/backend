package tw.commonground.backend.service.issue.entity;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IssueRepository extends JpaRepository<IssueEntity, UUID> {
    @Cacheable("issue")
    Page<SimpleIssueEntity> findAllIssueEntityBy(Pageable pageable);

    @Cacheable("issue")
    Optional<IssueEntity> findById(UUID id);

    @CacheEvict(value = "issue", allEntries = true)
    IssueEntity save(IssueEntity entity);

    @CacheEvict(value = "issue", allEntries = true)
    void deleteById(UUID id);
}
