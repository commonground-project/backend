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
import tw.commonground.backend.shared.entity.Reaction;
import tw.commonground.backend.service.viewpoint.entity.ViewpointEntity;
import tw.commonground.backend.service.viewpoint.entity.ViewpointReactionEntity;
import tw.commonground.backend.shared.pagination.PaginationMapper;
import tw.commonground.backend.shared.pagination.PaginationParser;
import tw.commonground.backend.shared.pagination.PaginationRequest;
import tw.commonground.backend.shared.pagination.WrappedPaginationResponse;
import tw.commonground.backend.shared.tracing.Traced;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Traced
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

    @GetMapping("/issue/{id}/viewpoints")
    public WrappedPaginationResponse<List<ViewpointResponse>> getViewpointsForIssue(
            @AuthenticationPrincipal FullUserEntity user,
            @PathVariable UUID id,
            @Valid PaginationRequest request) {

        Pageable pageable = paginationParser.parsePageable(request);
        Page<ViewpointEntity> pageViewpoints = viewpointService.getIssueViewpoints(id, pageable);

        if (user == null) {
            return getPaginationResponse(pageViewpoints);
        } else {
            return getPaginationResponse(user.getId(), pageViewpoints);
        }
    }

    @PostMapping("/issue/{id}/viewpoints")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ViewpointResponse> createViewpointForIssue(@AuthenticationPrincipal FullUserEntity user,
                                                                     @PathVariable UUID id,
                                                                     @RequestBody ViewpointRequest request) {
        ViewpointEntity viewpointEntity = viewpointService.createIssueViewpoint(id, request, user);
        List<FactEntity> facts = viewpointService.getFactsOfViewpoint(viewpointEntity.getId());
        Reaction reaction = viewpointService.getReactionForViewpoint(user.getId(), viewpointEntity.getId());

        ViewpointResponse response = ViewpointMapper.toResponse(viewpointEntity, reaction, facts);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/viewpoints")
    public WrappedPaginationResponse<List<ViewpointResponse>> getViewpoints(
            @AuthenticationPrincipal FullUserEntity user,
            @Valid PaginationRequest pagination) {

        Pageable pageable = paginationParser.parsePageable(pagination);
        Page<ViewpointEntity> pageViewpoints = viewpointService.getViewpoints(pageable);

        if (user == null) {
            return getPaginationResponse(pageViewpoints);
        } else {
            return getPaginationResponse(user.getId(), pageViewpoints);
        }
    }

    @PostMapping("/viewpoints")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ViewpointResponse> createViewpoint(@AuthenticationPrincipal FullUserEntity user,
                                                             @RequestBody ViewpointRequest request) {
        ViewpointEntity viewpointEntity = viewpointService.createViewpoint(request, user);
        List<FactEntity> facts = viewpointService.getFactsOfViewpoint(viewpointEntity.getId());

        ViewpointResponse response = ViewpointMapper.toResponse(viewpointEntity, Reaction.NONE, facts);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/viewpoint/{id}")
    public ResponseEntity<ViewpointResponse> getViewpoint(@AuthenticationPrincipal FullUserEntity user,
                                                          @PathVariable @NotNull UUID id) {
        ViewpointEntity viewpointEntity = viewpointService.getViewpoint(id);
        List<FactEntity> facts = viewpointService.getFactsOfViewpoint(viewpointEntity.getId());

        Reaction reaction = Reaction.NONE;
        if (user != null) {
            reaction = viewpointService.getReactionForViewpoint(user.getId(), viewpointEntity.getId());
        }

        ViewpointResponse response = ViewpointMapper.toResponse(viewpointEntity, reaction, facts);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/viewpoint/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ViewpointResponse> updateViewpoint(@AuthenticationPrincipal FullUserEntity user,
                                                             @PathVariable @NotNull UUID id,
                                                             @RequestBody ViewpointRequest updateRequest) {
        ViewpointEntity viewpointEntity = viewpointService.updateViewpoint(id, updateRequest);
        List<FactEntity> facts = viewpointService.getFactsOfViewpoint(viewpointEntity.getId());
        Reaction reaction = viewpointService.getReactionForViewpoint(user.getId(), viewpointEntity.getId());

        ViewpointResponse response = ViewpointMapper.toResponse(viewpointEntity, reaction, facts);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/viewpoint/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteViewPoint(@PathVariable @NotNull UUID id) {
        viewpointService.deleteViewpoint(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/viewpoint/{id}/reaction/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ViewpointReactionResponse> reactToViewPoint(
            @AuthenticationPrincipal FullUserEntity user,
            @PathVariable UUID id,
            @RequestBody ViewpointReactionRequest reactionRequest) {

        Long userId = user.getId();
        ViewpointReactionEntity reactionEntity = viewpointService.reactToViewpoint(userId, id,
                reactionRequest.getReaction());

        ViewpointReactionResponse response = ViewpointMapper.toReactionResponse(reactionEntity);
        return ResponseEntity.ok(response);
    }

    public WrappedPaginationResponse<List<ViewpointResponse>> getPaginationResponse(
            Long userId,
            Page<ViewpointEntity> pageViewpoints) {

        Map<UUID, List<FactEntity>> factsMap = viewpointService.getFactsForViewpoints(pageViewpoints.getContent()
                .stream().map(ViewpointEntity::getId).toList());

        Map<UUID, Reaction> reactionsMap = viewpointService.getReactionsForViewpoints(
                userId,
                pageViewpoints.getContent().stream().map(ViewpointEntity::getId).toList());

        List<ViewpointResponse> viewpointResponses = pageViewpoints.getContent()
                .stream()
                .map(viewpointEntity ->
                        ViewpointMapper.toResponse(viewpointEntity,
                                reactionsMap.getOrDefault(viewpointEntity.getId(), Reaction.NONE),
                                factsMap.getOrDefault(viewpointEntity.getId(), List.of())))
                .toList();

        return new WrappedPaginationResponse<>(viewpointResponses, PaginationMapper.toResponse(pageViewpoints));
    }

    public WrappedPaginationResponse<List<ViewpointResponse>> getPaginationResponse(
            Page<ViewpointEntity> pageViewpoints) {

        Map<UUID, List<FactEntity>> factsMap = viewpointService.getFactsForViewpoints(pageViewpoints.getContent()
                .stream().map(ViewpointEntity::getId).toList());

        List<ViewpointResponse> viewpointResponses = pageViewpoints.getContent()
                .stream()
                .map(viewpointEntity ->
                        ViewpointMapper.toResponse(viewpointEntity, Reaction.NONE,
                                factsMap.getOrDefault(viewpointEntity.getId(), List.of())))
                .toList();

        return new WrappedPaginationResponse<>(viewpointResponses, PaginationMapper.toResponse(pageViewpoints));
    }
}
