package tw.commonground.backend.service.reply.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tw.commonground.backend.service.fact.entity.FactEntity;

import java.util.List;
import java.util.UUID;

public interface ReplyFactRepository extends JpaRepository<ReplyFactEntity, UUID> {

    @Query("select rf.reply.id as replyId, rf.fact as fact "
            + "FROM ReplyFactEntity rf "
            + "where rf.reply.id in :replyIds")
    List<ReplyFactProjection> findFactsByReplyIds(@Param("replyIds") List<UUID> replyIds);

    @Query("select rf.fact from ReplyFactEntity rf where rf.reply.id = :replyId")
    List<FactEntity> findFactsByReplyId(@Param("replyId") UUID replyId);

    @Query("select rf.reply from ReplyFactEntity rf where rf.fact.id = :factId")
    List<ReplyFactEntity> findRepliesByFactId(@Param("factId") UUID factId);

    @Modifying
    @Query(value = "INSERT INTO reply_fact_entity (reply_id, fact_id) VALUES (?1, ?2)", nativeQuery = true)
    void saveByReplyIdAndFactId(UUID replyId, UUID factId);
}
