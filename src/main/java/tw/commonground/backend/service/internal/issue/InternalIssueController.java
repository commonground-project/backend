package tw.commonground.backend.service.internal.issue;

import org.springframework.web.bind.annotation.*;
import tw.commonground.backend.service.internal.issue.dto.InternalIssueResponse;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/internal/issues")
public class InternalIssueController {

    private final InternalIssueService internalIssueService;

    public InternalIssueController(InternalIssueService internalIssueService) {
        this.internalIssueService = internalIssueService;
    }

    @GetMapping
    public List<InternalIssueResponse> getIssues() {
        return internalIssueService.getIssues();
    }

    @GetMapping("/{issueId}")
    public InternalIssueResponse getIssue(@PathVariable UUID issueId) {
        return internalIssueService.getIssue(issueId);
    }
}
