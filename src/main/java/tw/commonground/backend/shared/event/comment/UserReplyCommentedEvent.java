package tw.commonground.backend.shared.event.comment;

import java.util.UUID;

public class UserReplyCommentedEvent extends UserCommentedEvent {
    public UserReplyCommentedEvent(Object source, Long userId, UUID entityId, String content) {
        super(source, userId, entityId, content);
    }
}
