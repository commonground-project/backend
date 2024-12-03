package tw.commonground.backend.service.viewpoint;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tw.commonground.backend.service.fact.entity.FactEntity;
import tw.commonground.backend.service.user.entity.FullUserEntity;
import tw.commonground.backend.service.viewpoint.dto.*;
import tw.commonground.backend.service.viewpoint.entity.ViewpointEntity;
import tw.commonground.backend.service.viewpoint.entity.ViewpointReactionEntity;
import tw.commonground.backend.shared.pagination.PaginationMapper;
import tw.commonground.backend.shared.pagination.PaginationParser;
import tw.commonground.backend.shared.pagination.PaginationRequest;
import tw.commonground.backend.shared.pagination.WrappedPaginationResponse;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class ViewpointController {
    private static final int MAX_SIZE = 200;

    private final ViewpointService viewpointService;

    private final Set<String> sortableColumn = Set.of("title", "createdAt", "updatedAt");

    private final PaginationParser paginationParser = new PaginationParser(sortableColumn, MAX_SIZE);

    public ViewpointController(ViewpointService viewpointService) {
        this.viewpointService = viewpointService;
    }

    @GetMapping("/viewpoints")
    public WrappedPaginationResponse<List<ViewpointResponse>> getViewpoints(@Valid PaginationRequest pagination) {
        Pageable pageable = paginationParser.parsePageable(pagination);
        Page<ViewpointEntity> pageViewpoints = viewpointService.getViewpoints(pageable);
        Map<UUID, List<FactEntity>> factsMap = viewpointService.getFactsForViewpoints(pageViewpoints.getContent()
                .stream().map(ViewpointEntity::getId).toList());

        List<ViewpointResponse> viewpointResponses = pageViewpoints.getContent()
                .stream()
                .map(viewpointEntity ->
                        ViewpointMapper.toResponse(viewpointEntity, factsMap.get(viewpointEntity.getId())))
                .toList();

        return new WrappedPaginationResponse<>(viewpointResponses, PaginationMapper.toResponse(pageViewpoints));
    }

    @PostMapping("/viewpoints")
    public ResponseEntity<ViewpointResponse> createViewpoint(@RequestBody ViewpointRequest request) {
        ViewpointEntity viewpointEntity = viewpointService.createViewpoint(request);
        List<FactEntity> facts = viewpointService.getFactsOfViewpoint(viewpointEntity.getId());

        ViewpointResponse response = ViewpointMapper.toResponse(viewpointEntity, facts);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/viewpoint/{id}")
    public ResponseEntity<ViewpointResponse> getViewpoint(@PathVariable @NotNull UUID id) {
        ViewpointEntity viewpointEntity = viewpointService.getViewpoint(id);
        List<FactEntity> facts = viewpointService.getFactsOfViewpoint(viewpointEntity.getId());

        ViewpointResponse response = ViewpointMapper.toResponse(viewpointEntity, facts);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/viewpoint/{id}")
    public ResponseEntity<ViewpointResponse> updateViewpoint(@PathVariable @NotNull UUID id,
                                                             @RequestBody ViewpointRequest updateRequest) {
        ViewpointEntity viewpointEntity = viewpointService.updateViewpoint(id, updateRequest);
        List<FactEntity> facts = viewpointService.getFactsOfViewpoint(viewpointEntity.getId());

        ViewpointResponse response = ViewpointMapper.toResponse(viewpointEntity, facts);
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
            @AuthenticationPrincipal FullUserEntity user,
            @PathVariable UUID id,
            @RequestBody ViewpointReactionRequest reactionRequest) {

        Long userId = user.getId();
        ViewpointReactionEntity reactionEntity = viewpointService.reactToViewpoint(userId, id, reactionRequest.getReaction());

        ViewpointReactionResponse response = ViewpointMapper.toReactionResponse(reactionEntity);
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
