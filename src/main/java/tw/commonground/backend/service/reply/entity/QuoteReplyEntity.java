package tw.commonground.backend.service.reply.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuoteReplyEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    private ReplyEntity reply;

    @Column(name = "start_pos")
    private Integer start;

    @Column(name = "end_pos")
    private Integer end;

}
