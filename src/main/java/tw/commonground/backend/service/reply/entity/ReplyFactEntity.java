package tw.commonground.backend.service.reply.entity;


import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.*;
import tw.commonground.backend.service.fact.entity.FactEntity;

@Getter
@Setter
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ReplyFactEntity {

    @EmbeddedId
    private ReplyFactKey key;

    @ManyToOne
    @MapsId("replyId")
    private ReplyEntity reply;

    @ManyToOne
    @MapsId("factId")
    private FactEntity fact;
}
