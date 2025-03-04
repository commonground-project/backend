package tw.commonground.backend.service.issue.entity;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IssueFollowRepository extends JpaRepository<IssueFollowEntity, IssueFollowKey> {

    @Cacheable("issueFollow")
    @Query("select i.follow from IssueFollowEntity i where i.id = :id")
    Optional<Boolean> findFollowById(IssueFollowKey id);

    @Cacheable("issueFollow")
    @Query("select i.user.id from IssueFollowEntity i where i.issue.id = :issueId and i.follow = true")
    Optional<List<Long>> findUsersIdByIssueIdAndFollowTrue(UUID issueId);

    @Caching(evict = {
            @CacheEvict(value = "issueFollow", allEntries = true),
            @CacheEvict(value = "issue", allEntries = true)
    })
    @Modifying
    @Query(value = "insert into issue_follow_entity (user_id, issue_id, follow, updated_at) "
            + "values (:#{#id.userId}, :#{#id.issueId}, :follow, current_timestamp)", nativeQuery = true)
    void insertFollowById(IssueFollowKey id, Boolean follow);

    @Caching(evict = {
            @CacheEvict(value = "issueFollow", allEntries = true),
            @CacheEvict(value = "issue", allEntries = true)
    })
    @Modifying
    @Query(value = "update issue_follow_entity set follow = :follow, updated_at = current_timestamp "
            + "where user_id = :#{#id.userId} and issue_id = :#{#id.issueId}", nativeQuery = true)
    void updateFollowById(IssueFollowKey id, Boolean follow);
}
