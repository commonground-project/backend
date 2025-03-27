package tw.commonground.backend.service.suggestion.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EditedTextResponseSuggestionItem {
    private String feedback;

    private String replacement;

    @JsonProperty("edited_message")
    private String editedMessage;
}
