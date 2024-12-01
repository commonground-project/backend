package tw.commonground.backend.service.jwt.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;
import tw.commonground.backend.service.jwt.JwtStateUtil;

import java.util.Base64;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class OAuthRequestResolver implements OAuth2AuthorizationRequestResolver {

    private final OAuth2AuthorizationRequestResolver defaultResolver;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Map<String, String> parameterMapping = Map.of(
            "r", "redirectUrl",
            "c", "callbackUrl"
    );

    private final JwtStateUtil jwtStateUtil;

    public OAuthRequestResolver(ClientRegistrationRepository clientRegistrationRepository, JwtStateUtil jwtStateUtil) {
        this.defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository,
                "/api/oauth2");
        this.jwtStateUtil = jwtStateUtil;
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        OAuth2AuthorizationRequest authorizationRequest = defaultResolver.resolve(request);
        return customizeAuthorizationRequest(request, authorizationRequest);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        OAuth2AuthorizationRequest authorizationRequest = defaultResolver.resolve(request, clientRegistrationId);
        return customizeAuthorizationRequest(request, authorizationRequest);
    }

    @SneakyThrows
    private OAuth2AuthorizationRequest customizeAuthorizationRequest(HttpServletRequest request,
                                                                     OAuth2AuthorizationRequest authorizationRequest) {
        if (authorizationRequest == null) {
            return null;
        }

        Map<String, String> stateMap = parameterMapping.entrySet().stream()
                .filter(entry -> request.getParameterMap().containsKey(entry.getKey()))
                .collect(Collectors.toMap(
                        Map.Entry::getValue,
                        entry -> request.getParameter(entry.getKey())
                ));

        stateMap.put("valid", jwtStateUtil.generateState());

        String state = Base64.getEncoder().encodeToString(objectMapper.writeValueAsString(stateMap).getBytes());

        OAuth2AuthorizationRequest.Builder builder = OAuth2AuthorizationRequest.from(authorizationRequest);
        builder.state(state);
        return builder.build();
    }
}
