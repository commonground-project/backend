package tw.commonground.backend.service.reply.entity;


import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@ToString
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class ReplyReactionKey implements Serializable {
    private Long userId;
    private UUID replyId;
}
