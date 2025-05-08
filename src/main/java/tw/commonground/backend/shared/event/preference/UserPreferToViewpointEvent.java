package tw.commonground.backend.shared.event.preference;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import tw.commonground.backend.shared.entity.Preference;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class UserPreferToViewpointEvent extends ApplicationEvent {

    private final LocalDateTime dateTime;

    private final Long userId;

    private final UUID viewpointId;

    private final Preference preference;

    public UserPreferToViewpointEvent(Object source,
                                      Long userId,
                                      UUID viewpointId,
                                      Preference preference) {
        super(source);

        this.dateTime = LocalDateTime.now();

        this.userId = userId;
        this.viewpointId = viewpointId;
        this.preference = preference;
    }
}
