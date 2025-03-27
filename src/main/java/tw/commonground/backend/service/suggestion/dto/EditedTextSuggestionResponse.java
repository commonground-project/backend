package tw.commonground.backend.service.suggestion.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class EditedTextSuggestionResponse {
    @JsonProperty("edited_text")
    private String editedText;

    @ToString.Exclude
    private List<EditedTextResponseSuggestionItem> suggestions;
}
