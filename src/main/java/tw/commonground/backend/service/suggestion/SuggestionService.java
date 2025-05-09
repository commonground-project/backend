package tw.commonground.backend.service.suggestion;

import org.springframework.beans.factory.annotation.Value;
import tw.commonground.backend.service.suggestion.dto.*;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SuggestionService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${mockApiUrl}")
    private String mockApiUrl;

    public SuggestionService() { }


    public TextSuggestionResponse getTextSuggestions(TextSuggestionRequest request) {
        String url = mockApiUrl + "/api/mock-text-suggestion";

        return makePostRequest(url, request, TextSuggestionResponse.class);
    }


    public EditedTextSuggestionResponse getEditedTextSuggestions(EditedTextSuggestionRequest request) {
        String url = mockApiUrl + "/api/mock-edited-text-suggestion";

        return makePostRequest(url, request, EditedTextSuggestionResponse.class);
    }


    private <T> T makePostRequest(String url, Object request, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> requestEntity = new HttpEntity<>(request, headers);
        ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, responseType);

        return response.getBody();
    }
}
