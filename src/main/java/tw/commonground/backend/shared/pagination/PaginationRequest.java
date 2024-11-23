package tw.commonground.backend.shared.pagination;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginationRequest {
    @NotNull
    private int page;
    @NotNull
    private int size;
    @NotEmpty
    private String sort;
}
