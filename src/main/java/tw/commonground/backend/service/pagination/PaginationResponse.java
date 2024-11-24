package tw.commonground.backend.service.pagination;

import lombok.*;

@Getter
@Setter
@Builder
public class PaginationResponse {
    private int size;
    private long totalElement;
    private int totalPage;
    private int number;
}
