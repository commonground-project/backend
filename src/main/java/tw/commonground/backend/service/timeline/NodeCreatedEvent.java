package tw.commonground.backend.service.timeline;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;
import tw.commonground.backend.service.issue.entity.IssueEntity;
import tw.commonground.backend.service.timeline.entity.NodeEntity;

@Getter
@Setter
@ToString
public class NodeCreatedEvent extends ApplicationEvent {

    private final IssueEntity issueEntity;

    private final NodeEntity nodeEntity;

    public NodeCreatedEvent(IssueEntity issueEntity, NodeEntity nodeEntity) {
        super(issueEntity);
        this.issueEntity = issueEntity;
        this.nodeEntity = nodeEntity;
    }
}
