package tw.commonground.backend.service.issue.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import tw.commonground.backend.service.viewpoint.dto.ViewpointResponseForAI;
import tw.commonground.backend.shared.pagination.PaginationResponse;

import java.util.List;

@AllArgsConstructor
@Setter
@Getter
public class IssueResponseForAI {
    private List<ViewpointResponseForAI> content;
    private PaginationResponse pagination;
}
