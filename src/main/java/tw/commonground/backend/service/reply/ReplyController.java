package tw.commonground.backend.service.reply;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tw.commonground.backend.service.fact.entity.FactEntity;
import tw.commonground.backend.service.reply.dto.*;
import tw.commonground.backend.service.reply.entity.Reaction;
import tw.commonground.backend.service.reply.entity.ReplyEntity;
import tw.commonground.backend.service.reply.entity.ReplyReactionEntity;
import tw.commonground.backend.service.subscription.exception.NotificationDeliveryException;
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
            @PathVariable @NotNull UUID id,
            @Valid PaginationRequest paginationRequest) {
        Pageable pageable = paginationParser.parsePageable(paginationRequest);
        Page<ReplyEntity> pageReplies = replyService.getViewpointReplies(id, pageable);

        return ResponseEntity.ok(getPaginationResponse(user.getId(), pageReplies));
    }

    @PostMapping("/viewpoint/{id}/replies")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ReplyResponse> createReplies(@AuthenticationPrincipal FullUserEntity user,
                                                       @PathVariable @NotNull UUID id,
                                                       @RequestBody @Valid ReplyRequest replyRequest)
        throws NotificationDeliveryException {
        ReplyEntity replyEntity = replyService.createViewpointReply(id, user, replyRequest);
        List<FactEntity> facts = replyService.getFactsOfReply(replyEntity.getId());
        return getReplyResponseResponseEntity(user, replyEntity, facts);
    }

    @GetMapping("/reply/{id}")
    public ResponseEntity<ReplyResponse> getReply(@AuthenticationPrincipal FullUserEntity user,
                                                  @PathVariable @NotNull UUID id) {
        ReplyEntity replyEntity = replyService.getReply(id);
        return getReplyResponseResponseEntity(user, id, replyEntity);
    }

    @PutMapping("/reply/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReplyResponse> updateReply(@AuthenticationPrincipal FullUserEntity user,
                                                     @PathVariable @NotNull UUID id,
                                                     @RequestBody @Valid ReplyRequest replyRequest) {
        ReplyEntity replyEntity = replyService.updateReply(id, replyRequest);
        return getReplyResponseResponseEntity(user, id, replyEntity);
    }

    @DeleteMapping("/reply/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteReply(@AuthenticationPrincipal FullUserEntity user, @PathVariable @NotNull UUID id) {
        replyService.deleteReply(id);
    }

    @PostMapping("/reply/{id}/reaction/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ReactionResponse> reactToReply(@AuthenticationPrincipal FullUserEntity user,
                                                         @PathVariable @NotNull UUID id,
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

        Map<UUID, List<QuoteReply>> quotesMap = replyService.getQuoteByReplies(pageReplies.getContent()
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
                                quotesMap.getOrDefault(replyEntity.getId(), List.of())
                                        .stream().map(replyService::getReplyByQuote).toList(),
                                quotesMap.getOrDefault(replyEntity.getId(), List.of())))
                .toList();

        return new WrappedPaginationResponse<>(replyResponses, PaginationMapper.toResponse(pageReplies));
    }

    private ResponseEntity<ReplyResponse> getReplyResponseResponseEntity(@AuthenticationPrincipal FullUserEntity user,
                                                                         @PathVariable @NotNull UUID id,
                                                                         ReplyEntity replyEntity) {
        List<FactEntity> facts = replyService.getFactsOfReply(id);
        return getReplyResponseResponseEntity(user, replyEntity, facts);
    }

    private ResponseEntity<ReplyResponse> getReplyResponseResponseEntity(@AuthenticationPrincipal FullUserEntity user,
                                                                         ReplyEntity replyEntity,
                                                                         List<FactEntity> facts) {
        List<QuoteReply> quotes = replyService.getQuotesOfReply(replyEntity.getId());
        List<ReplyEntity> replyEntities = replyService.getRepliesByQuotes(quotes);
        Reaction reaction = replyService.getReactionForReply(user.getId(), replyEntity.getId());

        return ResponseEntity.ok(ReplyMapper.toReplyResponse(replyEntity, reaction, facts, replyEntities, quotes));
    }
}
