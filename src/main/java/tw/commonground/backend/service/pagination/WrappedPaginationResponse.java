package tw.commonground.backend.service.pagination;

import lombok.*;

@Data
@AllArgsConstructor
public class WrappedPaginationResponse<T> {
    private T content;
    private PaginationResponse page;
}
