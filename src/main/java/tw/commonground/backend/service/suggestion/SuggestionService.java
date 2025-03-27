package tw.commonground.backend.service.suggestion;

import tw.commonground.backend.service.suggestion.dto.*;
import tw.commonground.backend.service.suggestion.entity.*;
import tw.commonground.backend.service.suggestion.dto.TextSuggestionMapper;
import tw.commonground.backend.service.suggestion.dto.EditedTextSuggestionMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class SuggestionService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String MOCK_API_URL = "http://localhost:5001";

    private final TextSuggestionRepository textSuggestionRepository;
    private final TextSuggestionDetailRepository textSuggestionDetailRepository;
    private final EditedTextSuggestionRepository editedTextSuggestionRepository;
    private final EditedTextSuggestionDetailRepository editedTextSuggestionDetailRepository;

    public SuggestionService(TextSuggestionRepository textSuggestionRepository,
                             TextSuggestionDetailRepository textSuggestionDetailRepository,
                             EditedTextSuggestionRepository editedTextSuggestionRepository,
                             EditedTextSuggestionDetailRepository editedTextSuggestionDetailRepository) {
        this.textSuggestionRepository = textSuggestionRepository;
        this.textSuggestionDetailRepository = textSuggestionDetailRepository;
        this.editedTextSuggestionRepository = editedTextSuggestionRepository;
        this.editedTextSuggestionDetailRepository = editedTextSuggestionDetailRepository;
    }


    public TextSuggestionResponse getTextSuggestions(TextSuggestionRequest request) {
        String url = MOCK_API_URL + "/api/mock-text-suggestion";
        TextSuggestionResponse response = makePostRequest(url, request, TextSuggestionResponse.class);

        TextSuggestionEntity textSuggestionEntity = textSuggestionRepository.save(
                TextSuggestionMapper.toEntity(request)
        );

        if (textSuggestionEntity.getSuggestions() == null) {
            textSuggestionEntity.setSuggestions(new ArrayList<>());
        }

        textSuggestionEntity.getSuggestions().clear();
        List<TextSuggestionDetailEntity> details = response.getSuggestions().stream()
                .map(suggestion -> TextSuggestionDetailEntity.builder()
                        .textSuggestion(textSuggestionEntity)
                        .message(suggestion.getMessage())
                        .feedback(suggestion.getFeedback())
                        .replacement(suggestion.getReplacement())
                        .build())
                .toList();

        textSuggestionDetailRepository.saveAll(details);
        textSuggestionEntity.getSuggestions().addAll(details);
        textSuggestionRepository.save(textSuggestionEntity);

        return response;
    }


    public EditedTextSuggestionResponse getEditedTextSuggestions(EditedTextSuggestionRequest request) {
        String url = MOCK_API_URL + "/api/mock-edited-text-suggestion";
        EditedTextSuggestionResponse response = makePostRequest(url, request, EditedTextSuggestionResponse.class);

        EditedTextSuggestionEntity editedTextSuggestionEntity = editedTextSuggestionRepository.save(
                EditedTextSuggestionMapper.toEntity(request)
        );

        if (editedTextSuggestionEntity.getSuggestions() == null) {
            editedTextSuggestionEntity.setSuggestions(new ArrayList<>());
        }

        editedTextSuggestionEntity.getSuggestions().clear();
        List<EditedTextSuggestionDetailEntity> details = response.getSuggestions().stream()
                .map(suggestion -> EditedTextSuggestionDetailEntity.builder()
                        .editedTextSuggestion(editedTextSuggestionEntity)
                        .editedMessage(suggestion.getEditedMessage())
                        .feedback(suggestion.getFeedback())
                        .replacement(suggestion.getReplacement())
                        .build())
                .toList();

        editedTextSuggestionDetailRepository.saveAll(details);
        editedTextSuggestionEntity.getSuggestions().addAll(details);
        editedTextSuggestionRepository.save(editedTextSuggestionEntity);

        return response;
    }


    private <T> T makePostRequest(String url, Object request, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> requestEntity = new HttpEntity<>(request, headers);
        ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, responseType);

        return response.getBody();
    }
}
