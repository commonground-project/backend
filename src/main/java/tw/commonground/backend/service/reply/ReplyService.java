package tw.commonground.backend.service.reply;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.commonground.backend.exception.EntityNotFoundException;
import tw.commonground.backend.service.fact.FactService;
import tw.commonground.backend.service.fact.entity.FactEntity;
import tw.commonground.backend.service.reply.dto.*;
import tw.commonground.backend.service.reply.entity.*;
import tw.commonground.backend.service.user.entity.FullUserEntity;
import tw.commonground.backend.service.viewpoint.ViewpointService;
import tw.commonground.backend.service.viewpoint.entity.ViewpointEntity;
import tw.commonground.backend.shared.content.ContentParser;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReplyService {

    private final ReplyRepository replyRepository;

    private final ReplyFactRepository replyFactRepository;

    private final ReplyReactionRepository replyReactionRepository;

    private final FactService factService;

    private final ViewpointService viewpointService;

    private final QuoteReplyRepository quoteReplyRepository;

    public ReplyService(ReplyRepository replyRepository,
                        ReplyFactRepository replyFactRepository,
                        ReplyReactionRepository replyReactionRepository,
                        FactService factService,
                        ViewpointService viewpointService,
                        QuoteReplyRepository quoteReplyRepository) {
        this.replyRepository = replyRepository;
        this.replyFactRepository = replyFactRepository;
        this.replyReactionRepository = replyReactionRepository;
        this.factService = factService;
        this.viewpointService = viewpointService;
        this.quoteReplyRepository = quoteReplyRepository;
    }

    public Page<ReplyEntity> getViewpointReplies(UUID id, Pageable pageable) {
        return replyRepository.findAllByViewpointId(id, pageable);
    }

    @Transactional
    public ReplyEntity createViewpointReply(UUID viewpointId, FullUserEntity user, ReplyRequest request) {
        factService.throwIfFactsNotExist(request.getFacts());
        viewpointService.throwIfViewpointNotExist(viewpointId);

        String content = ContentParser.convertLinkIntToUuid(request.getContent(),
                request.getFacts(),
                request.getQuotes().stream().map(QuoteReplyRequest::getReplyId).toList());

        ReplyEntity replyEntity = new ReplyEntity();
        replyEntity.setContent(content);
        replyEntity.setViewpoint(new ViewpointEntity(viewpointId));
        replyEntity.setAuthor(user);
        replyRepository.save(replyEntity);

        List<QuoteReplyEntity> quoteReplyEntities = addNewQuoteReply(request.getQuotes());
        quoteReplyRepository.saveAll(quoteReplyEntities);

        replyEntity.setQuotes(quoteReplyEntities);
        replyRepository.save(replyEntity);

        for (UUID factId : request.getFacts()) {
            replyFactRepository.saveByReplyIdAndFactId(replyEntity.getId(), factId);
        }

        return replyEntity;
    }

    public ReplyEntity getReply(UUID id) {
        return replyRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Reply", "id", id.toString())
        );
    }

    @Transactional
    public ReplyEntity updateReply(UUID id, ReplyRequest request) {
        ReplyEntity replyEntity = replyRepository.findById(id).orElseThrow(() -> {
            return new EntityNotFoundException("Reply", "id", id.toString());
        });

        List<QuoteReplyEntity> oldQuoteReplyEntities = quoteReplyRepository.findAllIdsByReplyId(id);
        quoteReplyRepository.deleteAll(oldQuoteReplyEntities);

        String content = request.getContent();
        replyEntity.setContent(content);

        List<QuoteReplyEntity> quoteReplyEntities = addNewQuoteReply(request.getQuotes());
        quoteReplyRepository.saveAll(quoteReplyEntities);

        replyEntity.setQuotes(quoteReplyEntities);
        replyRepository.save(replyEntity);

        for (UUID factId : request.getFacts()) {
            replyFactRepository.saveByReplyIdAndFactId(replyEntity.getId(), factId);
        }

        return replyEntity;
    }

    public void deleteReply(UUID id) {
        List<QuoteReplyEntity> quoteReplyEntities = quoteReplyRepository.findAllIdsByReplyId(id);
        quoteReplyRepository.deleteAll(quoteReplyEntities);
        replyRepository.deleteById(id);
    }

    public List<FactEntity> getFactsOfReply(UUID id) {
        return replyFactRepository.findFactsByReplyId(id);
    }

    public Map<UUID, List<FactEntity>> getFactsByReplies(List<UUID> replyIds) {
        List<ReplyFactProjection> results = replyFactRepository.findFactsByReplyIds(replyIds);
        return results.stream()
                .collect(Collectors.groupingBy(
                        ReplyFactProjection::getReplyId,
                        Collectors.mapping(ReplyFactProjection::getFact, Collectors.toList())
                ));
    }

    public Map<UUID, List<QuoteReplyEntity>> getQuoteByReplies(List<UUID> replyIds) {
        List<ReplyEntity> results = replyRepository.findAllByIds(replyIds);
        return results.stream().collect(Collectors.toMap(
                ReplyEntity::getId,
                ReplyEntity::getQuotes
        ));
    }

    public Reaction getReactionForReply(Long userId, UUID replyId) {
        ReplyReactionKey id = new ReplyReactionKey(userId, replyId);
        return replyReactionRepository.findReactionById(id).orElse(Reaction.NONE);
    }

    @Transactional
    public ReplyReactionEntity reactToReply(Long userId, UUID replyId, Reaction reaction) {
        throwIfReplyNotExist(replyId);

        ReplyReactionKey replyReactionKey = new ReplyReactionKey(userId, replyId);

        Optional<ReplyReactionEntity> reactionOptional = replyReactionRepository.findById(replyReactionKey);
        return reactionOptional
                .map(replyReactionEntity -> handleExistingReaction(replyReactionEntity, replyId, reaction))
                .orElseGet(() -> handleNewReaction(replyReactionKey, replyId, reaction));
    }

    private ReplyReactionEntity handleNewReaction(ReplyReactionKey reactionKey, UUID replyId, Reaction reaction) {
        if (reaction != Reaction.NONE) {
            replyReactionRepository.insertReaction(reactionKey, reaction.name());
            updateReactionCount(replyId, reaction, 1);
        }

        ReplyReactionEntity reactionEntity = new ReplyReactionEntity();
        reactionEntity.setId(reactionKey);
        reactionEntity.setReaction(reaction);

        return reactionEntity;
    }

    private ReplyReactionEntity handleExistingReaction(ReplyReactionEntity reactionEntity, UUID replyId,
                                                       Reaction newReaction) {

        Reaction previousReaction = reactionEntity.getReaction();
        if (previousReaction == newReaction) {
            return reactionEntity;
        }

        if (previousReaction == Reaction.NONE) {
            replyReactionRepository.updateReaction(reactionEntity.getId(), newReaction.name());
            updateReactionCount(replyId, newReaction, 1);
        } else {
            replyReactionRepository.updateReaction(reactionEntity.getId(), newReaction.name());
            updateReactionCount(replyId, previousReaction, -1);
            updateReactionCount(replyId, newReaction, 1);
        }

        reactionEntity.setReaction(newReaction);
        return reactionEntity;
    }

    public List<QuoteReplyEntity> getQuotesOfReply(UUID id) {
        return quoteReplyRepository.findAllIdsByReplyId(id);
    }

    public Map<UUID, Reaction> getReactionsForReplies(Long userId, List<UUID> replyIds) {
        List<ReplyReactionEntity> reactions = replyReactionRepository
                .findReactionsByUserIdAndReplyIds(userId, replyIds);

        return reactions.stream()
                .collect(Collectors.toMap(
                        ReplyReactionEntity::getReplyId,
                        ReplyReactionEntity::getReaction
                ));
    }

    private void throwIfReplyNotExist(UUID replyId) {
        if (!replyRepository.existsById(replyId)) {
            throw new EntityNotFoundException("Reply", "id", replyId.toString());
        }
    }

    private void updateReactionCount(UUID replyId, Reaction reaction, int delta) {
        replyRepository.updateReplyReaction(replyId, reaction, delta);
    }

    private List<QuoteReplyEntity> addNewQuoteReply(List<QuoteReplyRequest> quotesRequests) {
        List<QuoteReplyEntity> results = new ArrayList<>();
        for (QuoteReplyRequest quoteReplyRequest : quotesRequests) {
            QuoteReplyEntity quoteReplyEntity = new QuoteReplyEntity();
            quoteReplyEntity.setReply(replyRepository.findById(quoteReplyRequest.getReplyId()).orElseThrow(
                    () -> new EntityNotFoundException("Reply", "id", quoteReplyRequest.getReplyId().toString())
            ));
            quoteReplyEntity.setStart(quoteReplyRequest.getStart());
            quoteReplyEntity.setEnd(quoteReplyRequest.getEnd());
            results.add(quoteReplyEntity);
        }
        return results;
    }

}
