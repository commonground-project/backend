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
import tw.commonground.backend.shared.content.ContentReply;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReplyService {

    private final ReplyRepository replyRepository;

    private final ReplyFactRepository replyFactRepository;

    private final ReplyReactionRepository replyReactionRepository;

    private final FactService factService;

    private final ViewpointService viewpointService;

    public ReplyService(ReplyRepository replyRepository,
                        ReplyFactRepository replyFactRepository,
                        ReplyReactionRepository replyReactionRepository,
                        FactService factService,
                        ViewpointService viewpointService) {
        this.replyRepository = replyRepository;
        this.replyFactRepository = replyFactRepository;
        this.replyReactionRepository = replyReactionRepository;
        this.factService = factService;
        this.viewpointService = viewpointService;
    }

    public Page<ReplyEntity> getViewpointReplies(UUID id, Pageable pageable) {
        return replyRepository.findAllByViewpointId(id, pageable);
    }

    @Transactional
    public ReplyEntity createViewpointReply(UUID viewpointId, FullUserEntity user, ReplyRequest request) {
        factService.throwIfFactsNotExist(request.getFacts());
        viewpointService.throwIfViewpointNotExist(viewpointId);

        List<QuoteReply> quotes = request.getQuotes().stream().map(quote -> (QuoteReply) quote).toList();
        quotes.forEach(quote -> throwIfReplyNotExist(quote.getReplyId()));

        String content = ContentParser.convertLinkIntToUuid(request.getContent(),
                request.getFacts(),
                quotes);

        ReplyEntity replyEntity = new ReplyEntity();
        replyEntity.setContent(content);
        replyEntity.setViewpoint(new ViewpointEntity(viewpointId));
        replyEntity.setAuthor(user);
        replyRepository.save(replyEntity);

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
        ReplyEntity replyEntity = replyRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Reply", "id", id.toString()));

        List<QuoteReply> quotes = request.getQuotes().stream().map(quote -> (QuoteReply) quote).toList();
        quotes.forEach(quote -> throwIfReplyNotExist(quote.getReplyId()));

        String content = ContentParser.convertLinkIntToUuid(request.getContent(),
                request.getFacts(),
                quotes);

        replyEntity.setContent(content);

        replyRepository.save(replyEntity);

        for (UUID factId : request.getFacts()) {
            replyFactRepository.saveByReplyIdAndFactId(replyEntity.getId(), factId);
        }

        return replyEntity;
    }

    public void deleteReply(UUID id) {
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

    public Map<UUID, List<QuoteReply>> getQuoteByReplies(List<UUID> replyIds) {
        List<ReplyEntity> results = replyRepository.findAllByIds(replyIds);
        return results.stream().collect(Collectors.toMap(
                ReplyEntity::getId,
                replyEntity -> getQuotesOfReply(replyEntity.getId())
        ));
    }

    public List<ReplyEntity> getRepliesByQuotes(List<QuoteReply> quotes) {
        return replyRepository.findAllByIds(quotes.stream().map(QuoteReply::getReplyId).collect(Collectors.toList()));
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
        reactionEntity.setReply(replyRepository.findById(replyId).orElseThrow(
                () -> new EntityNotFoundException("Reply", "id", replyId.toString())
        ));

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

    public List<QuoteReply> getQuotesOfReply(UUID id) {
        ContentReply contentReply = ContentParser.separateContentAndReplies(replyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reply", "id", id.toString())).getContent());

        return contentReply.getQuotes();
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

    public ReplyEntity getReplyByQuote(QuoteReply quoteReply) {
        return replyRepository.findById(quoteReply.getReplyId()).orElseThrow(
                () -> new EntityNotFoundException("Reply", "id", quoteReply.getReplyId().toString())
        );
    }

    private void throwIfReplyNotExist(UUID replyId) {
        if (!replyRepository.existsById(replyId)) {
            throw new EntityNotFoundException("Reply", "id", replyId.toString());
        }
    }

    private void updateReactionCount(UUID replyId, Reaction reaction, int delta) {
        replyRepository.updateReplyReaction(replyId, reaction, delta);
    }

}
