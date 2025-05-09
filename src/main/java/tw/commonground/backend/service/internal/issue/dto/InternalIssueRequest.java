package tw.commonground.backend.service.internal.issue.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;


@Getter
@Setter
public class InternalIssueRequest {
    private String insight;

    public List<UUID> facts;
}
