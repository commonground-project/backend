package tw.commonground.backend.service.read;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tw.commonground.backend.service.read.dto.ReadMapper;
import tw.commonground.backend.service.read.dto.ReadRequest;
import tw.commonground.backend.service.read.dto.ReadResponse;
import tw.commonground.backend.service.read.entity.ReadEntity;
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

    @PutMapping("/read/{id}/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ReadResponse> updateReadStatus(@AuthenticationPrincipal FullUserEntity user,
                                                           @PathVariable UUID id,
                                                           @RequestBody ReadRequest request) {
        ReadEntity entity = readService.updateReadStatus(user.getId(), id, request);
        return ResponseEntity.ok(ReadMapper.toResponse(entity));
    }
}
