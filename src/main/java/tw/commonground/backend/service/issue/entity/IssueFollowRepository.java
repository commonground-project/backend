package tw.commonground.backend.service.issue.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IssueFollowRepository extends JpaRepository<IssueFollowEntity, IssueFollowKey> {
    @Query("select i.follow from IssueFollowEntity i where i.id = :id")
    Optional<Boolean> findFollowById(IssueFollowKey id);

    @Query("select i.user.id from IssueFollowEntity i where i.issue.id = :issueId and i.follow = true")
    Optional<List<Long>> findUsersIdByIssueIdAndFollowTrue(UUID issueId);
}
