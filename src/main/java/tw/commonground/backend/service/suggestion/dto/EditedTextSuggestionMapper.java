package tw.commonground.backend.service.suggestion.dto;

import tw.commonground.backend.service.suggestion.entity.EditedTextSuggestionDetailEntity;
import tw.commonground.backend.service.suggestion.entity.EditedTextSuggestionEntity;

public final class EditedTextSuggestionMapper {

    private EditedTextSuggestionMapper() {
        // hide constructor
    }

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
