package tw.commonground.backend.service.newcontent;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tw.commonground.backend.service.newcontent.dto.NewcontentMapper;
import tw.commonground.backend.service.newcontent.dto.NewcontentResponse;
import tw.commonground.backend.service.newcontent.dto.NewcontentRequest;
import tw.commonground.backend.service.newcontent.entity.NewcontentEntity;
import tw.commonground.backend.service.newcontent.entity.NewcontentObjectType;
import tw.commonground.backend.service.user.entity.FullUserEntity;
import tw.commonground.backend.shared.tracing.Traced;

import java.util.UUID;

@Traced
@RestController
@RequestMapping("/api")
public class NewcontentController {

    private final NewcontentService newcontentService;

    public NewcontentController(NewcontentService newcontentService) {
        this.newcontentService = newcontentService;
    }

    @PostMapping("/newcontent/issue/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<NewcontentResponse> updateNewcontentIssue(@AuthenticationPrincipal FullUserEntity user,
                                                                    @PathVariable UUID id,
                                                                    @RequestBody NewcontentRequest request) {
        boolean newcontentStatus = request.getNewcontentStatus();
        NewcontentEntity entity = newcontentService.updateNewcontentStatus(user.getId(), id, newcontentStatus, NewcontentObjectType.ISSUE);
        return ResponseEntity.ok(NewcontentMapper.toResponse(entity));
    }

    @GetMapping("/newcontent/issue/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<NewcontentResponse> newcontentIssue(@AuthenticationPrincipal FullUserEntity user,
                                                  @PathVariable UUID id) {
        NewcontentResponse response = newcontentService.getNewcontentStatus(user.getId(), id, NewcontentObjectType.ISSUE);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/newcontent/viewpoint/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<NewcontentResponse> newcontentViewpoint(@AuthenticationPrincipal FullUserEntity user,
                                                      @PathVariable UUID id,
                                                      @RequestBody NewcontentRequest request) {
        boolean newcontentStatus = request.getNewcontentStatus();
        NewcontentEntity entity = newcontentService.updateNewcontentStatus(user.getId(), id, newcontentStatus, NewcontentObjectType.VIEWPOINT);
        return ResponseEntity.ok(NewcontentMapper.toResponse(entity));
    }

    @GetMapping("/newcontent/viewpoint/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<NewcontentResponse> newcontentViewpoint(@AuthenticationPrincipal FullUserEntity user,
                                                      @PathVariable UUID id) {
        NewcontentResponse response = newcontentService.getNewcontentStatus(user.getId(), id, NewcontentObjectType.VIEWPOINT);
        return ResponseEntity.ok(response);
    }

}
