package  tw.commonground.backend.service.internal.viewpoint;

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
    public List<InternalViewpointResponse> getViewpoints() {
        return internalViewpointService.getViewpoints();
    }

    @GetMapping("/{viewpointId}")
    public InternalViewpointResponse getViewpointById(@PathVariable UUID viewpointId) {
        return internalViewpointService.getViewpointById(viewpointId);
    }
}
