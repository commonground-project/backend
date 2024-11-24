package tw.commonground.backend.service.viewpoint;

import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
//import tw.commonground.backend.service.fact.entity.FactEntity;
import tw.commonground.backend.service.viewpoint.dto.ViewpointMapper;
import tw.commonground.backend.service.viewpoint.dto.ViewpointReactionResponse;
import tw.commonground.backend.service.viewpoint.dto.ViewpointResponse;
import tw.commonground.backend.service.viewpoint.dto.ViewpointUpdateRequest;
import tw.commonground.backend.service.viewpoint.entity.ViewpointEntity;
import tw.commonground.backend.service.viewpoint.entity.ViewpointReaction;

import java.util.UUID;

@RestController
@RequestMapping("/api")
public class ViewpointController {
    private final ViewpointService viewpointService;

    public ViewpointController(ViewpointService viewpointService) {
        this.viewpointService = viewpointService;
    }

    // TODO: issue viewpoint api

    // TODO: wait for page and size
//    @GetMapping("/viewpoints")
//    public ResponseEntity<List<ViewpointResponse>> getViewPoints(
//            @RequestParam(required = false) String sort,
//            @RequestParam(required = false) Integer page,
//            @RequestParam(required = false) Integer size) {
//        // Implement the logic to get all viewpoints
//        return ResponseEntity.ok(viewpointService.getViewpoints(sort, page, size));
//    }

    @GetMapping("/viewpoint/{id}")
    public ResponseEntity<ViewpointResponse> getViewPoint(@PathVariable @NotBlank UUID id) {
        ViewpointResponse response = ViewpointMapper.toResponse(viewpointService.getViewpoint(id));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/viewpoint/{id}")
    public ResponseEntity<ViewpointResponse> updateViewPoint(
            @PathVariable @NotBlank UUID id,
            @RequestBody ViewpointUpdateRequest updateRequest) {
        ViewpointResponse response = ViewpointMapper.toResponse(viewpointService.updateViewpoint(id, updateRequest));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/viewpoint/{id}")
    public ResponseEntity<Void> deleteViewPoint(@PathVariable @NotBlank UUID id) {
        viewpointService.deleteViewpoint(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/viewpoint/{id}/reaction/me")
    public ResponseEntity<ViewpointReactionResponse> reactToViewPoint(
            @PathVariable @NotBlank UUID id,
            @RequestBody ViewpointReaction reaction) {
        ViewpointReactionResponse response = ViewpointMapper.toReactionResponse(viewpointService.reactToViewpoint(id, reaction));
        return ResponseEntity.ok(response);
    }

    // TODO: wait for page and size
//    @GetMapping("/viewpoint/{id}/facts")
//    public ResponseEntity<List<FactEntity>> getFactsOfViewPoint(
//            @PathVariable @NotBlank UUID id,
//            @RequestParam(required = false) Integer page,
//            @RequestParam(required = false) Integer size) {
//        // Implement the logic to get facts of a viewpoint
//        return ResponseEntity.ok(viewpointService.getFactsOfViewpoint(id, page, size));
//    }

//    @PostMapping("/viewpoint/{id}/facts")
//    public ResponseEntity<ViewpointResponse> addFactToViewPoint(
//            @PathVariable @NotBlank UUID id,
//            @RequestBody UUID factId) {
//        ViewpointResponse response = ViewpointMapper.toResponse(viewpointService.addFactToViewpoint(id, factId));
//        return ResponseEntity.ok(response);
//    }

//    @DeleteMapping("/viewpoint/{id}/facts/{factId}")
//    public ResponseEntity<Void> removeFactFromViewPoint(
//            @PathVariable @NotBlank UUID id,
//            @PathVariable @NotBlank UUID factId) {
//        viewpointService.deleteFactFromViewpoint(id, factId);
//        return ResponseEntity.noContent().build();
//    }
}
