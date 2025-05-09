package tw.commonground.backend.service.suggestion.dto;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TextSuggestionRequest {
    private String text;
}
