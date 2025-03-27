package tw.commonground.backend.service.suggestion.dto;

import tw.commonground.backend.service.suggestion.entity.TextSuggestionDetailEntity;
import tw.commonground.backend.service.suggestion.entity.TextSuggestionEntity;

public final class TextSuggestionMapper {

    private TextSuggestionMapper() {
        // hide constructor
    }

    public static TextSuggestionEntity toEntity(TextSuggestionRequest request) {
        return TextSuggestionEntity.builder()
                .text(request.getText())
                .build();
    }

    public static TextResponseSuggestionItem toResponse(TextSuggestionDetailEntity entity) {
        return TextResponseSuggestionItem.builder()
                .message(entity.getMessage())
                .feedback(entity.getFeedback())
                .replacement(entity.getReplacement())
                .build();
    }
}
