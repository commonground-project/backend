package tw.commonground.backend.service.internal.account.seurity;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import tw.commonground.backend.service.internal.account.ServiceAccountService;
import tw.commonground.backend.security.UserAuthentication;
import tw.commonground.backend.service.user.entity.FullUserEntity;

import java.io.IOException;

public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(TokenAuthenticationFilter.class);

    private final ServiceAccountService serviceAccountService;

    public TokenAuthenticationFilter(ServiceAccountService serviceAccountService) {
        this.serviceAccountService = serviceAccountService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                FullUserEntity user = serviceAccountService.authenticate(token);
                UserAuthentication authentication = new UserAuthentication(user);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                logger.debug("Invalid access token trying next filter, error: {}", e.getMessage());
            }
        }
        filterChain.doFilter(request, response);
    }
}
