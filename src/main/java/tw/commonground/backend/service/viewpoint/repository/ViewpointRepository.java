package tw.commonground.backend.service.viewpoint.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import tw.commonground.backend.service.viewpoint.entity.ViewpointEntity;
import tw.commonground.backend.shared.entity.Reaction;

import java.util.UUID;

public interface ViewpointRepository extends JpaRepository<ViewpointEntity, UUID>, ViewpointRepositoryCustom {

    Page<ViewpointEntity> findAllByIssueId(UUID issueId, Pageable pageable);

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
