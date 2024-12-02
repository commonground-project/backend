package tw.commonground.backend.shared.pagination;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("MagicNumber")
public class PaginationRequest {

    @Min(value = 0, message = "Page must be more than 0")
    private int page;

    @Min(value = 1, message = "Size must be more than 1")
    private int size;

    private String sort;
}
