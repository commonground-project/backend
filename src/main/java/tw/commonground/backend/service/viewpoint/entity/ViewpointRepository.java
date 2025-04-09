package tw.commonground.backend.service.viewpoint.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tw.commonground.backend.shared.entity.Reaction;

import java.util.UUID;

public interface ViewpointRepository extends JpaRepository<ViewpointEntity, UUID>, ViewpointRepositoryCustom {

    Page<ViewpointEntity> findAllByIssueId(UUID issueId, Pageable pageable);

    @Query("SELECT v FROM ViewpointEntity v "
            + "LEFT JOIN ViewpointSimilarityEntity vs ON v.id = vs.key.viewpointId AND vs.key.userId = :userId "
            + "WHERE v.issue.id = :issueId "
            + "ORDER BY "
            + "CASE WHEN vs.similarity IS NOT NULL THEN 0 ELSE 1 END, "
            + "vs.similarity DESC, "
            + "v.createdAt DESC")
    Page<ViewpointEntity> findAllWithSimilarityByIssueID(Long userId, UUID issueId, Pageable pageable);

}

interface ViewpointRepositoryCustom {

    void updateReactionCount(UUID viewpointId, Reaction reaction, int delta);

}

class ViewpointRepositoryImpl implements ViewpointRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void updateReactionCount(UUID viewpointId, Reaction reaction, int delta) {
        String column = getColumnForReaction(reaction);
        if (column == null) {
            return;
        }

        String sql = buildUpdateQuery(column);
        executeUpdateQuery(sql, viewpointId, delta);
    }

    private String getColumnForReaction(Reaction reaction) {
        return switch (reaction) {
            case NONE -> null;
            case LIKE -> "like_count";
            case DISLIKE -> "dislike_count";
            case REASONABLE -> "reasonable_count";
        };
    }

    private String buildUpdateQuery(String column) {
        return "UPDATE viewpoint_entity SET " + column + " = " + column + " + :delta WHERE id = :viewpointId";
    }

    private void executeUpdateQuery(String sql, UUID viewpointId, int delta) {
        entityManager.createNativeQuery(sql)
                .setParameter("delta", delta)
                .setParameter("viewpointId", viewpointId)
                .executeUpdate();
    }
}
