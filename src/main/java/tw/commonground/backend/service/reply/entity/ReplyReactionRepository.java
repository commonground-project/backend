package tw.commonground.backend.service.reply.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import tw.commonground.backend.shared.entity.Reaction;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReplyReactionRepository extends JpaRepository<ReplyReactionEntity, ReplyReactionKey> {

    @Query("SELECT r.reaction FROM ReplyReactionEntity r WHERE r.id = :id")
    Optional<Reaction> findReactionById(ReplyReactionKey id);

    @Query("SELECT r FROM ReplyReactionEntity r WHERE r.user.id = :userId AND r.reply.id IN :replyIds")
    List<ReplyReactionEntity> findReactionsByUserIdAndReplyIds(Long userId, List<UUID> replyIds);

    @Modifying
    @Query(value = "INSERT INTO reply_reaction_entity (reply_id, user_id, reaction) "
            + "VALUES (:#{#id.replyId}, :#{#id.userId}, :reaction)", nativeQuery = true)
    void insertReaction(ReplyReactionKey id, String reaction);

    @Modifying
    @Query(value = "UPDATE reply_reaction_entity SET reaction = :reaction "
            + "WHERE reply_id = :#{#id.replyId} AND user_id = :#{#id.userId}", nativeQuery = true)
    void updateReaction(ReplyReactionKey id, String reaction);
}
