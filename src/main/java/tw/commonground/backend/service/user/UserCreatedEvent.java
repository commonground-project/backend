package tw.commonground.backend.service.user;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import tw.commonground.backend.service.user.entity.UserEntity;
import tw.commonground.backend.shared.tracing.Traced;

@Traced
@Getter
public class UserCreatedEvent extends ApplicationEvent {

    private final UserEntity userEntity;

    public UserCreatedEvent(UserEntity userEntity) {
        super(userEntity);
        this.userEntity = userEntity;
    }
}
