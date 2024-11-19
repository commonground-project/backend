package tw.commonground.backend.shared.pagination;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WrappedPaginationResponse<T> {
    private T content;
    private PaginationResponse page;
}
