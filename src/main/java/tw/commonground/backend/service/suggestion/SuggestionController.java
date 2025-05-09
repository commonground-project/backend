package tw.commonground.backend.service.suggestion;

import tw.commonground.backend.service.suggestion.dto.*;
import org.springframework.web.bind.annotation.*;
import tw.commonground.backend.shared.tracing.Traced;

@Traced
@RestController
@RequestMapping("/api")
public class SuggestionController {

    private final SuggestionService suggestionService;

    public SuggestionController(SuggestionService suggestionService) {
        this.suggestionService = suggestionService;
    }

    @PostMapping("/text-suggestion")
    public TextSuggestionResponse getTextSuggestions(@RequestBody TextSuggestionRequest request) {
        return suggestionService.getTextSuggestions(request);
    }

    @PostMapping("/edited-text-suggestion")
    public EditedTextSuggestionResponse getEditedTextSuggestions(@RequestBody EditedTextSuggestionRequest request) {
        return suggestionService.getEditedTextSuggestions(request);
    }
}
