package tw.commonground.backend.service.internal.issue;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tw.commonground.backend.service.internal.issue.dto.InternalIssueResponse;
import tw.commonground.backend.shared.tracing.Traced;

import java.util.List;
import java.util.UUID;

@Traced
@RestController
@RequestMapping("/api/internal/issues")
public class InternalIssueController {

    private final InternalIssueService internalIssueService;

    public InternalIssueController(InternalIssueService internalIssueService) {
        this.internalIssueService = internalIssueService;
    }

    @GetMapping
    public ResponseEntity<List<InternalIssueResponse>> getIssues() {
        List<InternalIssueResponse> issues = internalIssueService.getIssues();
        return ResponseEntity.ok(issues);
    }

    @GetMapping("/{issueId}")
    public ResponseEntity<InternalIssueResponse> getIssue(@PathVariable UUID issueId) {
        InternalIssueResponse issue = internalIssueService.getIssue(issueId);
        return ResponseEntity.ok(issue);
    }
}
