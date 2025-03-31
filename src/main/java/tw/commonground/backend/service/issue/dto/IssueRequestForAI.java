package tw.commonground.backend.service.issue.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class IssueRequestForAI {
    private UUID id;
}
