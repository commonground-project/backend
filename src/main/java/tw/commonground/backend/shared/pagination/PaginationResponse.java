package tw.commonground.backend.shared.pagination;

import lombok.*;

@Builder
public class PaginationResponse {
    private int size;
    private long totalElement;
    private int totalPage;
    private int number;
}
