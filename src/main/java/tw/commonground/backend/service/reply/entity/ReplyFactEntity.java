package tw.commonground.backend.service.reply.entity;


import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tw.commonground.backend.service.fact.entity.FactEntity;

@Getter
@Setter
@Entity
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
