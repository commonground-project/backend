package tw.commonground.backend.service.internal.viewpoint;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tw.commonground.backend.exception.EntityNotFoundException;
import tw.commonground.backend.service.internal.viewpoint.dto.InternalReplyMapper;
import tw.commonground.backend.service.internal.viewpoint.dto.InternalReplyResponse;
import tw.commonground.backend.service.internal.viewpoint.dto.InternalViewpointMapper;
import tw.commonground.backend.service.internal.viewpoint.dto.InternalViewpointResponse;
import tw.commonground.backend.service.reply.entity.ReplyRepository;
import tw.commonground.backend.service.viewpoint.entity.ViewpointEntity;
import tw.commonground.backend.service.viewpoint.entity.ViewpointRepository;
import tw.commonground.backend.shared.tracing.Traced;

import java.util.List;
import java.util.UUID;

@Traced
@Service
public class InternalViewpointService {
    private final ViewpointRepository viewpointRepository;
    private final ReplyRepository replyRepository;

    public InternalViewpointService(ViewpointRepository viewpointRepository, ReplyRepository replyRepository) {
        this.viewpointRepository = viewpointRepository;
        this.replyRepository = replyRepository;
    }

    public List<InternalViewpointResponse> getViewpoints() {
        List<ViewpointEntity> viewpoints = viewpointRepository.findAll();
        return viewpoints.stream()
                .map(this::convertToResponse)
                .toList();
    }

    public InternalViewpointResponse getViewpointById(UUID viewpointId) {
        ViewpointEntity viewpoint = viewpointRepository.findById(viewpointId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "InternalViewpoint",
                        "viewpoint id",
                        viewpointId.toString())
                );

        int replyCount = countRepliesByViewpointId(viewpointId);
        List<InternalReplyResponse> replyContents = getReplyContentsByViewpointId(viewpointId);

        return InternalViewpointMapper.toResponse(viewpoint, replyCount, replyContents);
    }

    private InternalViewpointResponse convertToResponse(ViewpointEntity viewpoint) {
        UUID viewpointId = viewpoint.getId();

        int replyCount = countRepliesByViewpointId(viewpointId);
        List<InternalReplyResponse> replyContents = getReplyContentsByViewpointId(viewpointId);

        return InternalViewpointMapper.toResponse(viewpoint, replyCount, replyContents);
    }

    public int countRepliesByViewpointId(UUID viewpointId) {
        return (int) replyRepository.findAllByViewpointId(viewpointId, Pageable.unpaged()).getTotalElements();
    }

    public List<InternalReplyResponse> getReplyContentsByViewpointId(UUID viewpointId) {
        return replyRepository.findAllByViewpointId(viewpointId, Pageable.unpaged())
                .getContent()
                .stream()
                .map(InternalReplyMapper::toResponse)
                .toList();
    }
}
