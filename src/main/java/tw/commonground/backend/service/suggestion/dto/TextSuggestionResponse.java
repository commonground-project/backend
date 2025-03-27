package tw.commonground.backend.service.suggestion.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TextSuggestionResponse {
    private String text;

    @ToString.Exclude
    private List<TextResponseSuggestionItem> suggestions;
}
