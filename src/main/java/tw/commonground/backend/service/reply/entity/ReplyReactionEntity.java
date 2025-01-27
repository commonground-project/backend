package tw.commonground.backend.service.reply.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tw.commonground.backend.service.user.entity.UserEntity;

import java.util.UUID;

@Getter
@Setter
@Entity
public class ReplyReactionEntity {

    @EmbeddedId
    private ReplyReactionKey id;

    @Enumerated(EnumType.STRING)
    private Reaction reaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("replyId")
    private ReplyEntity reply;

    public ReplyReactionEntity() {
        this.reaction = Reaction.NONE;
    }

    public ReplyReactionEntity(UserEntity user, ReplyEntity reply, Reaction reaction) {
        this.id = new ReplyReactionKey(user.getId(), reply.getId());
        this.user = user;
        this.reply = reply;
        this.reaction = reaction;
    }

    public UUID getReplyId() {
        return id.getReplyId();
    }

}
