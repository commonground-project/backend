package tw.commonground.backend.service.internal.viewpoint.dto;

import tw.commonground.backend.service.viewpoint.entity.ViewpointEntity;

import java.util.List;

public final class InternalViewpointMapper {

    private InternalViewpointMapper() {
        // hide constructor
    }

    // ViewpointEntity to InternalViewpointResponse
    public static InternalViewpointResponse toResponse(
            ViewpointEntity viewpoint, int replyCount, List<InternalReplyResponse> replies) {
        return InternalViewpointResponse.fromEntity(viewpoint, replyCount, replies);
    }
}
