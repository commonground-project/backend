package tw.commonground.backend.shared.event.preference;

import org.springframework.context.ApplicationEvent;
import tw.commonground.backend.shared.entity.Preference;

import java.util.UUID;

public class UserPreferToViewpointEvent extends ApplicationEvent {

    private final Long userId;

    private final UUID viewpointId;

    private final Preference preference;

    public UserPreferToViewpointEvent(Object source, Long userId, UUID viewpointId, Preference preference) {
        super(source);

        this.userId = userId;
        this.viewpointId = viewpointId;
        this.preference = preference;
    }
}
