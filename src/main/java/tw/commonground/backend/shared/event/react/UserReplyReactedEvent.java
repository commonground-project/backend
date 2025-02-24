package tw.commonground.backend.shared.event.react;

import tw.commonground.backend.shared.entity.Reaction;

import java.util.UUID;

public class UserReplyReactedEvent extends UserReactedEvent {
    public UserReplyReactedEvent(Object source, Long userId, UUID entityId, Reaction reaction) {
        super(source, userId, entityId, reaction);
    }
}
