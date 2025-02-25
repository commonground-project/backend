package tw.commonground.backend.shared.pagination;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class WrappedPaginationResponse<T> {
    private T content;
    private PaginationResponse page;
}
