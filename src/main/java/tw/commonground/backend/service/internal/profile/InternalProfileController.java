package tw.commonground.backend.service.internal.profile;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tw.commonground.backend.service.internal.profile.dto.InternalProfileResponse;
import tw.commonground.backend.shared.tracing.Traced;

import java.util.List;
import java.util.UUID;

@Traced
@RestController
@RequestMapping("/api/internal/users/profile")
public class InternalProfileController {

    private final InternalProfileService internalProfileService;

    public InternalProfileController(InternalProfileService internalProfileService) {
        this.internalProfileService = internalProfileService;
    }

    @GetMapping
    public ResponseEntity<List<InternalProfileResponse>> getProfiles() {
        List<InternalProfileResponse> profiles = internalProfileService.getProfiles();
        return ResponseEntity.ok(profiles);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<InternalProfileResponse> getProfile(@PathVariable UUID userId) {
        InternalProfileResponse profile = internalProfileService.getProfile(userId);
        return ResponseEntity.ok(profile);
    }
}
