package tw.commonground.backend.service.follow;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tw.commonground.backend.service.follow.dto.FollowMapper;
import tw.commonground.backend.service.follow.dto.FollowRequest;
import tw.commonground.backend.service.follow.dto.FollowResponse;
import tw.commonground.backend.service.follow.entity.FollowEntity;
import tw.commonground.backend.service.issue.IssueService;
import tw.commonground.backend.service.issue.dto.IssueMapper;
import tw.commonground.backend.service.user.entity.FullUserEntity;
import tw.commonground.backend.shared.tracing.Traced;

import java.util.UUID;


@Traced
@RestController
@RequestMapping("/api")
public class FollowController {

    private final IssueService issueService;
    private final FollowService followService;

    public FollowController(IssueService issueService, FollowService followService) {
        this.issueService = issueService;
        this.followService = followService;
    }

    @PostMapping("/issue/{id}/follow/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<FollowResponse> followIssue(@AuthenticationPrincipal FullUserEntity user,
                                                      @PathVariable UUID id,
                                                      @RequestBody FollowRequest request) {
        FollowEntity entity = followService.followIssue(user.getId(), id, request.getFollow());
        return ResponseEntity.ok(FollowMapper.toFollowResponse(entity));
    }

    @PostMapping("/viewpoint/{id}/follow/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<FollowResponse> followViewpoint(@AuthenticationPrincipal FullUserEntity user,
                                                           @PathVariable UUID id,
                                                           @RequestBody FollowRequest request) {
        FollowEntity entity = followService.followViewpoint(user.getId(), id, request.getFollow());
        return ResponseEntity.ok(FollowMapper.toFollowResponse(entity));
    }
}
