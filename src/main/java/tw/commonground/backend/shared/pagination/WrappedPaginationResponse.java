package tw.commonground.backend.shared.pagination;

import lombok.*;

@Data
@AllArgsConstructor
public class WrappedPaginationResponse<T> {
    private T content;
    private PaginationResponse page;
}
