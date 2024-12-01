package tw.commonground.backend.service.jwt.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tw.commonground.backend.service.jwt.JwtService;
import tw.commonground.backend.service.jwt.JwtStateUtil;
import tw.commonground.backend.service.jwt.dto.RefreshTokenResponse;
import tw.commonground.backend.service.user.entity.FullUserEntity;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

@Component
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final JwtStateUtil jwtStateUtil;

    @Value("${app.callbackUrl:}")
    private String callbackUrlConfig;

    @Value("${feature.debug.jwt.custom-callback-url.enabled:false}")
    private boolean enableCustomRedirection;

    @Value("${feature.debug.jwt.verify-state.disabled:false}")
    private boolean disableStateVerification;

    public OAuthSuccessHandler(JwtService jwtService, JwtStateUtil jwtStateUtil) {
        this.jwtService = jwtService;
        this.jwtStateUtil = jwtStateUtil;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String callbackUrlResult = callbackUrlConfig;

        String state = request.getParameter("state");
        StringBuilder callbackParam = new StringBuilder();
        if (state != null) {
            String decodedState = new String(Base64.getDecoder().decode(state));
            Map<String, String> stateData = objectMapper.readValue(decodedState, Map.class);
            String stateValue = stateData.get("valid");
            String redirectUrl = stateData.get("redirectUrl");
            String callbackUrl = stateData.get("callbackUrl");

            if (!disableStateVerification && !jwtStateUtil.verifyState(stateValue)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("State parameter is invalid");
                return;
            }

            if (enableCustomRedirection && callbackUrl != null) {
                callbackUrlResult = callbackUrl;
            }

            if (redirectUrl != null) {
                callbackParam.append("?r=").append(redirectUrl);
            }
        } else if (!disableStateVerification) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("State parameter is missing");
            return;
        }

        if (callbackUrlResult.isEmpty()) {
            String baseUrl = ServletUriComponentsBuilder.fromRequestUri(request)
                    .replacePath(null)
                    .build()
                    .toUriString();

            callbackUrlResult = baseUrl + "/completed-sign-in.html";
        }

        if (authentication.getPrincipal() instanceof OAuth2User oAuth2User) {
            FullUserEntity user = oAuth2User.getAttribute("entity");
            RefreshTokenResponse token = jwtService.generateTokens(user);

            callbackParam.append(!callbackParam.isEmpty() ? "&" : "?");
            callbackParam.append("token=").append(token.getAccessToken());
            callbackParam.append("&refreshToken=").append(token.getRefreshToken());

            response.sendRedirect(callbackUrlResult + callbackParam);
        }
    }
}
