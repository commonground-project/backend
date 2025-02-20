package tw.commonground.backend.shared.event.react;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import tw.commonground.backend.shared.entity.Reaction;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class UserReactedEvent extends ApplicationEvent {

    private final LocalDateTime dateTime;

    private final Long userId;

    private final UUID entityId;

    private final Reaction reaction;

    public UserReactedEvent(Object source, Long userId, UUID entityId, Reaction reaction) {
        super(source);
        this.userId = userId;
        this.entityId = entityId;
        this.reaction = reaction;

        this.dateTime = LocalDateTime.now();
    }
}