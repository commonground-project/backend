package tw.commonground.backend.pagination;

import org.springframework.data.domain.Page;

public final class PaginationMapper {

    private PaginationMapper() {
        // hide the constructor
    }

    public static PaginationResponse toResponse(Page<?> page) {
        return PaginationResponse.builder()
                .totalElement(page.getTotalElements())
                .totalPage(page.getTotalPages())
                .number(page.getNumber())
                .size(page.getSize())
                .build();
    }
}
