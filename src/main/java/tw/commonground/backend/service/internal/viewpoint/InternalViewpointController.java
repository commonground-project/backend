package  tw.commonground.backend.service.internal.viewpoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tw.commonground.backend.service.internal.viewpoint.dto.InternalViewpointResponse;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/internal/viewpoints")
public class InternalViewpointController {
    private final InternalViewpointService internalViewpointService;

    public InternalViewpointController(InternalViewpointService internalViewpointService) {
        this.internalViewpointService = internalViewpointService;
    }

    @GetMapping
    public ResponseEntity<List<InternalViewpointResponse>> getViewpoints() {
        List<InternalViewpointResponse> viewpoints = internalViewpointService.getViewpoints();
        return ResponseEntity.ok(viewpoints);
    }

    @GetMapping("/{viewpointId}")
    public ResponseEntity<InternalViewpointResponse> getViewpointById(@PathVariable UUID viewpointId) {
        InternalViewpointResponse viewpoint = internalViewpointService.getViewpointById(viewpointId);
        return ResponseEntity.ok(viewpoint);
    }
}
