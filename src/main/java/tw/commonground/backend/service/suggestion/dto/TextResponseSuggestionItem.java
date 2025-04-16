package tw.commonground.backend.service.suggestion.dto;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TextResponseSuggestionItem {
    private String message;

    private String feedback;

    private String replacement;
}
