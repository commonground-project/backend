package tw.commonground.backend.service.pagination;

import org.springframework.data.domain.Page;

public class PaginationMapper {
    public PaginationResponse toResponse(Page page) {
        return PaginationResponse.builder()
                .totalElement(page.getTotalElements())
                .totalPage(page.getTotalPages())
                .number(page.getNumber())
                .size(page.getSize())
                .build();
    }
}
