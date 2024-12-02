package tw.commonground.backend.service.viewpoint;

import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.web.bind.annotation.*;
//import tw.commonground.backend.service.fact.entity.FactEntity;
import tw.commonground.backend.service.user.entity.FullUserEntity;
import tw.commonground.backend.service.viewpoint.dto.*;
import tw.commonground.backend.service.viewpoint.entity.ViewpointReactionEntity;

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

    // test
    @PostMapping("/viewpointadd")
    public ResponseEntity<ViewpointResponse> getViewPoint(@RequestBody ViewpointUpdateRequest updateRequest) {
        ViewpointResponse response = ViewpointMapper.toResponse(viewpointService.createViewpoint(updateRequest));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/viewpoint/{id}")
    public ResponseEntity<ViewpointResponse> getViewPoint(@PathVariable @NotNull UUID id) {
        ViewpointResponse response = ViewpointMapper.toResponse(viewpointService.getViewpoint(id));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/viewpoint/{id}")
    public ResponseEntity<ViewpointResponse> updateViewPoint(
            @PathVariable @NotNull UUID id,
            @RequestBody ViewpointUpdateRequest updateRequest) {
        ViewpointResponse response = ViewpointMapper.toResponse(viewpointService.updateViewpoint(id, updateRequest));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/viewpoint/{id}")
    public ResponseEntity<Void> deleteViewPoint(@PathVariable @NotNull UUID id) {
        viewpointService.deleteViewpoint(id);
        return ResponseEntity.noContent().build();
    }


    @PreAuthorize("isAuthenticated()")
    @PostMapping("/viewpoint/{id}/reaction/me")
    public ResponseEntity<ViewpointReactionResponse> reactToViewPoint(
            @AuthenticationPrincipal DefaultOAuth2User user,
            @PathVariable @NotNull UUID id,
            @RequestBody ViewpointReactionRequest reactionRequest) {
        String email = user.getName();
        ViewpointReactionResponse response = ViewpointMapper.toReactionResponse(viewpointService.reactToViewpoint(email, id, reactionRequest.getReaction()));
        return ResponseEntity.ok(response);
    }

    // TODO: wait for page and size
//    @GetMapping("/viewpoint/{id}/facts")
//    public ResponseEntity<List<FactEntity>> getFactsOfViewPoint(
//            @PathVariable @NotNull UUID id,
//            @RequestParam(required = false) Integer page,
//            @RequestParam(required = false) Integer size) {
//        // Implement the logic to get facts of a viewpoint
//        return ResponseEntity.ok(viewpointService.getFactsOfViewpoint(id, page, size));
//    }

//    @PostMapping("/viewpoint/{id}/facts")
//    public ResponseEntity<ViewpointResponse> addFactToViewPoint(
//            @PathVariable @NotNull UUID id,
//            @RequestBody UUID factId) {
//        ViewpointResponse response = ViewpointMapper.toResponse(viewpointService.addFactToViewpoint(id, factId));
//        return ResponseEntity.ok(response);
//    }

//    @DeleteMapping("/viewpoint/{id}/facts/{factId}")
//    public ResponseEntity<Void> removeFactFromViewPoint(
//            @PathVariable @NotNull UUID id,
//            @PathVariable @NotNull UUID factId) {
//        viewpointService.deleteFactFromViewpoint(id, factId);
//        return ResponseEntity.noContent().build();
//    }
}
