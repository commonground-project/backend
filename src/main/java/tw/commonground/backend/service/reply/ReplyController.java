package tw.commonground.backend.service.reply;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tw.commonground.backend.service.fact.entity.FactEntity;
import tw.commonground.backend.service.reply.dto.*;
import tw.commonground.backend.service.reply.entity.QuoteReplyEntity;
import tw.commonground.backend.service.reply.entity.Reaction;
import tw.commonground.backend.service.reply.entity.ReplyEntity;
import tw.commonground.backend.service.reply.entity.ReplyReactionEntity;
import tw.commonground.backend.service.user.entity.FullUserEntity;
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
public class ReplyController {

    private static final int MAX_SIZE = 200;

    private final ReplyService replyService;

    private final Set<String> sortableColumn = Set.of("createdAt");

    private final PaginationParser paginationParser = new PaginationParser(sortableColumn, MAX_SIZE);

    public ReplyController(ReplyService replyService) {
        this.replyService = replyService;
    }

    @GetMapping("/viewpoint/{id}/replies")
    public ResponseEntity<WrappedPaginationResponse<List<ReplyResponse>>> getReplies(
            @AuthenticationPrincipal FullUserEntity user,
            @PathVariable UUID id,
            @Valid PaginationRequest paginationRequest) {
        Pageable pageable = paginationParser.parsePageable(paginationRequest);
        Page<ReplyEntity> pageReplies = replyService.getViewpointReplies(id, pageable);

        return ResponseEntity.ok(getPaginationResponse(user.getId(), pageReplies));
    }

    @PostMapping("/viewpoint/{id}/replies")
    public ResponseEntity<ReplyResponse> createReplies(@AuthenticationPrincipal FullUserEntity user,
                                                             @PathVariable UUID id,
                                                             @RequestBody ReplyRequest replyRequest) {
        ReplyEntity replyEntity = replyService.createViewpointReply(id, user, replyRequest);
        List<FactEntity> facts = replyService.getFactsOfReply(replyEntity.getId());
        List<QuoteReplyEntity> quotes = replyService.getQuotesOfReply(replyEntity.getId());
        Reaction reaction = replyService.getReactionForReply(user.getId(), replyEntity.getId());

        return ResponseEntity.ok(ReplyMapper.toReplyResponse(replyEntity, reaction, facts, quotes));
    }

    @GetMapping("/reply/{id}")
    public ResponseEntity<ReplyResponse> getReply(@AuthenticationPrincipal FullUserEntity user,
                                                  @PathVariable UUID id) {
        ReplyEntity replyEntity = replyService.getReply(id);
        List<FactEntity> facts = replyService.getFactsOfReply(id);
        List<QuoteReplyEntity> quotes = replyService.getQuotesOfReply(replyEntity.getId());
        Reaction reaction = replyService.getReactionForReply(user.getId(), replyEntity.getId());

        return ResponseEntity.ok(ReplyMapper.toReplyResponse(replyEntity, reaction, facts, quotes));
    }

    @PutMapping("/reply/{id}")
    public ResponseEntity<ReplyResponse> updateReply(@AuthenticationPrincipal FullUserEntity user,
                                                     @PathVariable UUID id,
                                                     @RequestBody ReplyRequest replyRequest) {
        ReplyEntity replyEntity = replyService.updateReply(id, replyRequest);
        List<FactEntity> facts = replyService.getFactsOfReply(id);
        List<QuoteReplyEntity> quotes = replyService.getQuotesOfReply(replyEntity.getId());
        Reaction reaction = replyService.getReactionForReply(user.getId(), replyEntity.getId());

        return ResponseEntity.ok(ReplyMapper.toReplyResponse(replyEntity, reaction, facts, quotes));
    }

    @DeleteMapping("/reply/{id}")
    public void deleteReply(@AuthenticationPrincipal FullUserEntity user, @PathVariable UUID id) {
        replyService.deleteReply(id);
    }

//    @PreAuthorize("isAuthenticated()")
    @PostMapping("/reply/{id}/reaction/me")
    public ResponseEntity<ReactionResponse> reactToReply(@AuthenticationPrincipal FullUserEntity user,
                                                           @PathVariable UUID id,
                                                           @RequestBody ReactionRequest reactionRequest) {
        Long userId = user.getId();
        ReplyReactionEntity replyReactionEntity = replyService.reactToReply(userId, id, reactionRequest.getReaction());

        return ResponseEntity.ok(ReplyMapper.toReactionResponse(replyReactionEntity));
    }

    private WrappedPaginationResponse<List<ReplyResponse>> getPaginationResponse(
            Long userId,
            Page<ReplyEntity> pageReplies) {

        Map<UUID, List<FactEntity>> factsMap = replyService.getFactsByReplies(pageReplies.getContent()
                .stream().map(ReplyEntity::getId).toList());

        Map<UUID, List<QuoteReplyEntity>> quotesMap = replyService.getQuoteByReplies(pageReplies.getContent()
            .stream().map(ReplyEntity::getId).toList());

        Map<UUID, Reaction> reactionsMap = replyService.getReactionsForReplies(
                userId,
                pageReplies.getContent().stream().map(ReplyEntity::getId).toList());

        List<ReplyResponse> replyResponses = pageReplies.getContent()
                .stream()
                .map(replyEntity ->
                        ReplyMapper.toReplyResponse(replyEntity,
                                reactionsMap.getOrDefault(replyEntity.getId(), Reaction.NONE),
                                factsMap.getOrDefault(replyEntity.getId(), List.of()),
                                quotesMap.getOrDefault(replyEntity.getId(), List.of())))
                .toList();

        return new WrappedPaginationResponse<>(replyResponses, PaginationMapper.toResponse(pageReplies));
    }
}
