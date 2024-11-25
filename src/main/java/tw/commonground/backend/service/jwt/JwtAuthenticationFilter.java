package tw.commonground.backend.service.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tw.commonground.backend.service.user.entity.FullUserEntity;

import java.io.IOException;

import static tw.commonground.backend.exception.ProblemTemplate.INVALID_ACCESS_TOKEN;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                FullUserEntity user = jwtService.authenticate(token);
                JwtAuthentication authentication = new JwtAuthentication(user);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                SecurityContextHolder.clearContext();

                response.setStatus(HttpStatus.FORBIDDEN.value());
                response.setContentType("application/problem+json");
                response.getWriter().write(INVALID_ACCESS_TOKEN);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
