package tw.commonground.backend.service.internal.viewpoint.dto;

import tw.commonground.backend.service.reply.entity.ReplyEntity;

public final class InternalReplyMapper {
    private InternalReplyMapper() {
        // hide constructor
    }

    public static InternalReplyResponse toResponse(ReplyEntity reply) {
        return InternalReplyResponse.builder()
                .id(reply.getId())
                .authorId(reply.getAuthorId())
                .content(reply.getContent())
                .likeCount(reply.getLikeCount())
                .dislikeCount(reply.getDislikeCount())
                .reasonableCount(reply.getReasonableCount())
                .createdAt(reply.getCreatedAt())
                .build();
    }
}
