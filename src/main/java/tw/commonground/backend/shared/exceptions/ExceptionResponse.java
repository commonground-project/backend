package tw.commonground.backend.shared.exceptions;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionResponse extends Exception {
    private String type;
    private int status;
    private String title;
    private String detail;
    private String instance;
}
