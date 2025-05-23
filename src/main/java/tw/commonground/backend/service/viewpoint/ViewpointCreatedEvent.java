package tw.commonground.backend.service.viewpoint;

import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;
import tw.commonground.backend.service.viewpoint.entity.ViewpointEntity;
import tw.commonground.backend.service.user.entity.FullUserEntity;

@Getter
@ToString
public class ViewpointCreatedEvent extends ApplicationEvent {

    private final FullUserEntity user;

    private final ViewpointEntity viewpointEntity;

    private final String issueTitle;

    public ViewpointCreatedEvent(FullUserEntity user, ViewpointEntity viewpointEntity, String issueTitle) {
        super(viewpointEntity);
        this.user = user;
        this.viewpointEntity = viewpointEntity;
        this.issueTitle = issueTitle;
    }
}
