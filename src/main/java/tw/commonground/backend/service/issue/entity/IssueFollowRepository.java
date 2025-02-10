package tw.commonground.backend.service.issue.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface IssueFollowRepository extends JpaRepository<IssueFollowEntity, IssueFollowKey> {
    @Query("select i.follow from IssueFollowEntity i where i.id = :id")
    Optional<Boolean> findFollowById(IssueFollowKey id);
}
