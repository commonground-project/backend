package tw.commonground.backend.service.read;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tw.commonground.backend.service.read.dto.ReadMapper;
import tw.commonground.backend.service.read.dto.ReadResponse;
import tw.commonground.backend.service.read.entity.ReadEntity;
import tw.commonground.backend.service.read.entity.ReadObjectType;
import tw.commonground.backend.service.user.entity.FullUserEntity;
import tw.commonground.backend.shared.tracing.Traced;

import java.util.UUID;

@Traced
@RestController
@RequestMapping("/api")
public class ReadController {

    private final ReadService readService;

    public ReadController(ReadService readService) {
        this.readService = readService;
    }

    @PostMapping("/read/issue/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ReadResponse> readIssue(@AuthenticationPrincipal FullUserEntity user,
                                                  @PathVariable UUID id) {
        ReadEntity entity = readService.updateReadStatus(user.getId(), id, ReadObjectType.ISSUE);
        return ResponseEntity.ok(ReadMapper.toResponse(entity));
    }
    // since the POST method is used to set the read status to TRUE, we don't need to pass the request body

    @GetMapping("/read/issue/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ReadResponse> getReadIssue(@AuthenticationPrincipal FullUserEntity user,
                                                     @PathVariable UUID id) {
        Boolean readStatus = readService.getReadStatus(user.getId(), id, ReadObjectType.ISSUE);
        ReadResponse response = ReadMapper.toResponse(user.getId(), id, readStatus);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/read/viewpoint/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ReadResponse> readViewpoint(@AuthenticationPrincipal FullUserEntity user,
                                                      @PathVariable UUID id) {
        ReadEntity entity = readService.updateReadStatus(user.getId(), id, ReadObjectType.VIEWPOINT);
        return ResponseEntity.ok(ReadMapper.toResponse(entity));
    }

    @GetMapping("/read/viewpoint/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ReadResponse> getReadViewpoint(@AuthenticationPrincipal FullUserEntity user,
                                                         @PathVariable UUID id) {
        Boolean readStatus = readService.getReadStatus(user.getId(), id, ReadObjectType.VIEWPOINT);
        ReadResponse response = ReadMapper.toResponse(user.getId(), id, readStatus);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/read/reply/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ReadResponse> readReply(@AuthenticationPrincipal FullUserEntity user,
                                                  @PathVariable UUID id) {
        ReadEntity entity = readService.updateReadStatus(user.getId(), id, ReadObjectType.REPLY);
        return ResponseEntity.ok(ReadMapper.toResponse(entity));
    }

    @GetMapping("/read/reply/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ReadResponse> getReadReply(@AuthenticationPrincipal FullUserEntity user,
                                                     @PathVariable UUID id) {
        Boolean readStatus = readService.getReadStatus(user.getId(), id, ReadObjectType.REPLY);
        ReadResponse response = ReadMapper.toResponse(user.getId(), id, readStatus);
        return ResponseEntity.ok(response);
    }

}
