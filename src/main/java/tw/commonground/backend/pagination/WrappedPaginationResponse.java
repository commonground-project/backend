package tw.commonground.backend.pagination;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class WrappedPaginationResponse<T> {
    private T content;
    private PaginationResponse page;
}
