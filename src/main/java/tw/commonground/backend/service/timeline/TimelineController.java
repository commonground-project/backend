package tw.commonground.backend.service.timeline;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tw.commonground.backend.service.timeline.dto.*;
import tw.commonground.backend.service.timeline.entity.NodeEntity;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class TimelineController {

    private final TimelineService timelineService;

    public TimelineController(TimelineService timelineService) {
        this.timelineService = timelineService;
    }

    @GetMapping("/issue/{id}/timeline")
    public ResponseEntity<TimelineResponse> getTimeline(@PathVariable UUID id) {
        List<NodeEntity> nodes = timelineService.getNodes(id);
        TimelineResponse response = TimelineMapper.toResponse(nodes);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/issue/{id}/timeline")
    public ResponseEntity<NodeResponse> createNode(@PathVariable UUID id, @Valid @RequestBody NodeRequest request) {
        NodeEntity node = timelineService.createNode(id, request);
        NodeResponse response = NodeMapper.toResponse(node);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/timeline/node/{nodeId}")
    public ResponseEntity<NodeResponse> getNode(@PathVariable UUID nodeId) {
        NodeEntity node = timelineService.getNode(nodeId);
        NodeResponse response = NodeMapper.toResponse(node);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/timeline/node/{nodeId}")
    public ResponseEntity<NodeResponse> updateNode(@PathVariable UUID nodeId, @Valid @RequestBody NodeRequest request) {
        NodeEntity node = timelineService.updateNode(nodeId, request);
        NodeResponse response = NodeMapper.toResponse(node);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/timeline/node/{nodeId}")
    public ResponseEntity<Void> deleteNode(@PathVariable UUID nodeId) {
        timelineService.deleteNode(nodeId);
        return ResponseEntity.noContent().build();
    }
}
