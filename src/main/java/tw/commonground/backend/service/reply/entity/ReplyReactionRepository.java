package tw.commonground.backend.service.reply.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReplyReactionRepository extends JpaRepository<ReplyReactionEntity, ReplyReactionKey> {

    @Query("select r.reaction from ReplyReactionEntity r where r.id = :id")
    Optional<Reaction> findReactionById(ReplyReactionKey id);

    @Query("select r from ReplyReactionEntity r where r.user.id = :userId and r.reply.id in :replyIds")
    List<ReplyReactionEntity> findReactionsByUserIdAndReplyIds(Long userId, List<UUID> replyIds);

    @Query(value = "insert into reply_reaction_entity (reply_id, user_id, reaction) "
            + "values (:#{#id.replyId}, :#{#id.userId}), :reaction", nativeQuery = true)
    void insertReaction(ReplyReactionKey id, String reaction);

    @Modifying
    @Query(value = "update reply_reaction_key set reaction = :reaction "
            + "where reply_id = :#{#id.replyId} and user_id :#{#id.userId}", nativeQuery = true)
    void updateReaction(ReplyReactionKey id, String reaction);
}
