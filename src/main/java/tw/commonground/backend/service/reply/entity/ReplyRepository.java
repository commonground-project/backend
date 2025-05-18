package tw.commonground.backend.service.reply.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tw.commonground.backend.shared.entity.Reaction;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReplyRepository extends JpaRepository<ReplyEntity, UUID>, ReplyRepositoryCustom {

    @Query("SELECT r FROM ReplyEntity r WHERE r.id IN :ids")
    List<ReplyEntity> findAllByIds(@Param("ids") List<UUID> ids);

    Integer countByViewpointId(UUID viewpointId);

    Page<ReplyEntity> findAllByViewpointId(UUID viewpointId, Pageable pageable);

    @Query("SELECT r FROM ReplyEntity r JOIN FETCH r.viewpoint v JOIN FETCH v.issue WHERE r.id = :id")
    Optional<ReplyEntity> findByIdWithViewpointAndIssue(@Param("id") UUID id);
}

interface ReplyRepositoryCustom {

    void updateReplyReaction(UUID replyId, Reaction reaction, int delta);

}

class ReplyRepositoryImpl implements ReplyRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void updateReplyReaction(UUID replyId, Reaction reaction, int delta) {
        String column = getColumnForReaction(reaction);
        if (column == null) {
            return;
        }

        String sql = buildQuery(column);
        executeUpdateQuery(sql, replyId, delta);
    }

    private String getColumnForReaction(Reaction reaction) {
        return switch (reaction) {
            case NONE -> null;
            case LIKE -> "like_count";
            case REASONABLE -> "reasonable_count";
            case DISLIKE -> "dislike_count";
        };
    }

    private String buildQuery(String column) {
        return "UPDATE reply_entity SET " + column + " = " + column + " + :delta WHERE id = :replyId";
    }

    private void executeUpdateQuery(String sql, UUID replyId, int delta) {
        entityManager.createNativeQuery(sql)
                .setParameter("delta", delta)
                .setParameter("replyId", replyId)
                .executeUpdate();
    }
}
