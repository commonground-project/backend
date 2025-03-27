package tw.commonground.backend.service.suggestion.dto;

import tw.commonground.backend.service.suggestion.entity.EditedTextSuggestionDetailEntity;
import tw.commonground.backend.service.suggestion.entity.EditedTextSuggestionEntity;

public class EditedTextSuggestionMapper {

    public static EditedTextSuggestionEntity toEntity(EditedTextSuggestionRequest request) {
        return EditedTextSuggestionEntity.builder()
                .editedText(request.getEditedText())
                .build();
    }

    public static EditedTextResponseSuggestionItem toResponse(EditedTextSuggestionDetailEntity entity) {
        return EditedTextResponseSuggestionItem.builder()
                .editedMessage(entity.getEditedMessage())
                .feedback(entity.getFeedback())
                .replacement(entity.getReplacement())
                .build();
    }
}
