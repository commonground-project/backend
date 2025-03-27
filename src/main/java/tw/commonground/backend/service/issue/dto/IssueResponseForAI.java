package tw.commonground.backend.service.issue.dto;

import tw.commonground.backend.service.viewpoint.dto.ViewpointResponseForAI;
import tw.commonground.backend.shared.pagination.PaginationResponse;

import java.util.List;

public class IssueResponseForAI {
    private List<ViewpointResponseForAI> content;
    private PaginationResponse pagination;
}
