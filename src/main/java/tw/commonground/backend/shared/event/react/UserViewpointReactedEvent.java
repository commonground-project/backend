package tw.commonground.backend.shared.event.react;

import tw.commonground.backend.shared.entity.Reaction;

import java.util.UUID;

public class UserViewpointReactedEvent extends UserReactedEvent {
    public UserViewpointReactedEvent(Object source, Long userId, UUID entityId, Reaction reaction) {
        super(source, userId, entityId, reaction);
    }
}
