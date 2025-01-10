package tw.commonground.backend.service.reply.dto;

import tw.commonground.backend.service.fact.dto.FactMapper;
import tw.commonground.backend.service.fact.dto.FactResponse;
import tw.commonground.backend.service.fact.entity.FactEntity;
import tw.commonground.backend.service.reply.entity.*;
import tw.commonground.backend.shared.content.ContentParser;
import tw.commonground.backend.shared.content.ContentReply;

import java.util.List;

public final class ReplyMapper {

    private ReplyMapper() {
        // hide constructor
    }

    public static QuoteReplyResponse quoteReplyToQuoteResponse(QuoteReplyEntity quoteReplyEntity, ReplyEntity replyEntity) {

        int start = quoteReplyEntity.getStart();
        int end = quoteReplyEntity.getEnd();

        return QuoteReplyResponse.builder()
                .replyId(quoteReplyEntity.getReply().getId())
                .authorId(quoteReplyEntity.getReply().getAuthorId())
                .authorName(quoteReplyEntity.getReply().getAuthorName())
                .authorAvatar(quoteReplyEntity.getReply().getAuthorAvatar())
                .content(replyEntity.getContent().substring(start, end))
                .start(start)
                .end(end)
                .build();
    }

    public static ReplyResponse toReplyResponse(ReplyEntity replyEntity,
                                                Reaction reaction,
                                                List<FactEntity> factEntities,
                                                List<QuoteReplyEntity> quoteReplyEntities) {

        //Quotes
        List<QuoteReplyResponse> quotes = quoteReplyEntities.stream()
                .map(quote -> quoteReplyToQuoteResponse(quote, replyEntity))
                .toList();

        //Facts
        List<FactResponse> facts = factEntities.stream().map(FactMapper::toResponse).toList();

        ContentReply content = ContentParser.separateContentAndReplies(replyEntity.getContent(),
                facts.stream().map(FactResponse::getId).toList(),
                quotes.stream().map(QuoteReplyResponse::getReplyId).toList());


        return ReplyResponse.builder()
                .id(replyEntity.getId())
                .userReaction(ReplyReactionResponse.builder().reaction(reaction).build())
                .createdAt(replyEntity.getCreatedAt())
                .updatedAt(replyEntity.getUpdatedAt())
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
                .updatedAt(reactionEntity.getReply().getUpdatedAt())
                .build();

    }
}
