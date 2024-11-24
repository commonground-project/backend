package tw.commonground.backend.service.pagination;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginationRequest {

    @Min(value = 0, message = "Page should more than 0")
    private int page;
    @Min(value = 1, message = "Size should more than 1")
    private int size;

    private String sort;
}
