package tw.commonground.backend.service.reply.entity;


import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ReplyFactKey implements Serializable {

    private UUID replyId;

    private UUID factId;

}
