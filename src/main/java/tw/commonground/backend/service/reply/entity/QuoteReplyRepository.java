package tw.commonground.backend.service.reply.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface QuoteReplyRepository extends JpaRepository<QuoteReplyEntity, UUID> {

    @Query("select qr from QuoteReplyEntity qr where qr.reply.id = :replyId")
    List<QuoteReplyEntity> findAllIdsByReplyId(@Param("replyId") UUID replyId);

}
