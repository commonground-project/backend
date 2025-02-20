package tw.commonground.backend.service.reply.dto;

import tw.commonground.backend.service.fact.dto.FactMapper;
import tw.commonground.backend.service.fact.dto.FactResponse;
import tw.commonground.backend.service.fact.entity.FactEntity;
import tw.commonground.backend.service.reply.entity.*;
import tw.commonground.backend.shared.content.ContentParser;
import tw.commonground.backend.shared.content.ContentReply;
import tw.commonground.backend.shared.entity.Reaction;
import tw.commonground.backend.shared.util.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;

public final class ReplyMapper {

    private ReplyMapper() {
        // hide constructor
    }

    public static QuoteReplyResponse quoteReplyToQuoteResponse(QuoteReply quoteReply, ReplyEntity replyEntity) {

        int start = quoteReply.getStart();
        int end = quoteReply.getEnd();

        return QuoteReplyResponse.builder()
                .replyId(quoteReply.getReplyId())
                .authorId(replyEntity.getAuthorId())
                .authorName(replyEntity.getAuthorName())
                .authorAvatar(replyEntity.getAuthorAvatar())
                .content(replyEntity.getContent().substring(start, end))
                .start(start)
                .end(end)
                .build();
    }

    public static ReplyResponse toReplyResponse(ReplyEntity replyEntity,
                                                Reaction reaction,
                                                List<FactEntity> factEntities,
                                                List<ReplyEntity> replyEntities,
                                                List<QuoteReply> quoteReplies) {

        List<FactResponse> facts = factEntities.stream().map(FactMapper::toResponse).toList();

        ContentReply content = ContentParser.separateContentAndReplies(replyEntity.getContent(),
                facts.stream().map(FactResponse::getId).toList());

        List<QuoteReplyResponse> quotes = new ArrayList<>();

        for (int i = 0; i < replyEntities.size(); i++) {
            quotes.add(quoteReplyToQuoteResponse(quoteReplies.get(i), replyEntities.get(i)));
        }


        return ReplyResponse.builder()
                .id(replyEntity.getId())
                .userReaction(ReplyReactionResponse.builder().reaction(reaction).build())
                .createdAt(DateTimeUtils.toIso8601String(replyEntity.getCreatedAt()))
                .updatedAt(DateTimeUtils.toIso8601String(replyEntity.getUpdatedAt()))
                .authorId(replyEntity.getAuthorId())
                .authorName(replyEntity.getAuthorName())
                .authorAvatar(replyEntity.getAuthorAvatar())
                .content(content.getText())
                .likeCount(replyEntity.getLikeCount())
                .reasonableCount(replyEntity.getReasonableCount())
                .dislikeCount(replyEntity.getDislikeCount())
                .quotes(quotes)
                .facts(facts)
                .build();
    }

    public static ReactionResponse toReactionResponse(ReplyReactionEntity reactionEntity) {
        return ReactionResponse.builder()
                .reaction(reactionEntity.getReaction())
                .likeCount(reactionEntity.getReply().getLikeCount())
                .dislikeCount(reactionEntity.getReply().getDislikeCount())
                .reasonableCount(reactionEntity.getReply().getReasonableCount())
                .updatedAt(DateTimeUtils.toIso8601String(reactionEntity.getReply().getUpdatedAt()))
                .build();

    }
}
