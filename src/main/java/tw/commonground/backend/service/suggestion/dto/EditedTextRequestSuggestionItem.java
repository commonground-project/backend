package tw.commonground.backend.service.suggestion.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EditedTextRequestSuggestionItem {
    private String message;

    private String feedback;

    private String replacement;

    @JsonProperty("edited_message")
    private String editedMessage;
}
