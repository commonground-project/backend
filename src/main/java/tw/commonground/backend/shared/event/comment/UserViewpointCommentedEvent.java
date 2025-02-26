package tw.commonground.backend.shared.event.comment;

import java.util.UUID;

public class UserViewpointCommentedEvent extends UserCommentedEvent {
    public UserViewpointCommentedEvent(Object source, Long userId, UUID entityId, String content) {
        super(source, userId, entityId, content);
    }
}
