package tw.commonground.backend.shared.event.comment;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class UserCommentedEvent extends ApplicationEvent {

    private final LocalDateTime dateTime;

    private final Long userId;

    private final UUID entityId;

    private final String content;

    public UserCommentedEvent(Object source, Long userId, UUID entityId, String content) {
        super(source);
        this.userId = userId;
        this.entityId = entityId;
        this.content = content;

        this.dateTime = LocalDateTime.now();
    }
}
