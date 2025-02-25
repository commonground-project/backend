package tw.commonground.backend.shared.pagination;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
public class PaginationResponse {
    private int size;
    private long totalElement;
    private int totalPage;
    private int number;
}
