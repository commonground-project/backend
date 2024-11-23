package tw.commonground.backend.shared.pagination;

import lombok.*;

@Data
@Builder
public class PaginationResponse {
    private int size;
    private long totalElement;
    private int totalPage;
    private int number;
}
