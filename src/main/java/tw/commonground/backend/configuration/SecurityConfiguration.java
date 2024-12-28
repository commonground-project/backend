package tw.commonground.backend.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import tw.commonground.backend.exception.ProblemTemplate;
import tw.commonground.backend.service.jwt.security.JwtAuthenticationFilter;
import tw.commonground.backend.service.jwt.JwtService;
import tw.commonground.backend.service.jwt.security.OAuthRequestResolver;
import tw.commonground.backend.service.jwt.security.OAuthSuccessHandler;
import tw.commonground.backend.service.user.UserService;
import tw.commonground.backend.service.user.dto.UserInitRequest;
import tw.commonground.backend.service.user.entity.FullUserEntity;
import tw.commonground.backend.service.user.entity.UserRole;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

@EnableMethodSecurity(securedEnabled = true)
@EnableWebSecurity
@Configuration
public class SecurityConfiguration {

    private final Logger logger = LoggerFactory.getLogger(SecurityConfiguration.class);

    private final UserService userService;

    private final JwtService jwtService;

    private final OAuthSuccessHandler successHandler;

    private final OAuthRequestResolver requestResolver;

    public SecurityConfiguration(UserService userService, JwtService jwtService,
                                 OAuthSuccessHandler successHandler, OAuthRequestResolver requestResolver) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.successHandler = successHandler;
        this.requestResolver = requestResolver;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtService), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/user/avatar/**").permitAll()
                        .requestMatchers("/api/jwt/refresh/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/api/setup/**").hasRole("SETUP_REQUIRED")
                        .requestMatchers("/api/debug/**").anonymous()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/completed-sign-in.html").permitAll()
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/**").permitAll()
                        .requestMatchers("/static/**").permitAll()
                        .anyRequest().authenticated())
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(this.oauth2UserService())
                        )
                        .authorizationEndpoint(authorization -> authorization
                                .authorizationRequestResolver(requestResolver)
                        )
                        .successHandler(successHandler)
                )
                .logout(logout -> logout
                        .logoutUrl("/api/logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration configuration = new CorsConfiguration();
                    configuration.setAllowedOrigins(List.of("*"));
                    configuration.setAllowedMethods(List.of("*"));
                    configuration.setAllowedHeaders(List.of("*"));
                    return configuration;
                }))

                // Only handle exceptions thrown by Spring Security
                .exceptionHandling(this::exceptionHandling);
        return http.build();
    }

    private void exceptionHandling(ExceptionHandlingConfigurer<HttpSecurity> exceptionHandling) {
        exceptionHandling
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType("application/problem+json");
                    response.getWriter().write(ProblemTemplate.UNAUTHORIZED);
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.setContentType("application/problem+json");
                    response.getWriter().write(ProblemTemplate.FORBIDDEN);
                });
    }

    /**
     * Parse OAuth2 user, then fetch user from database or create new user if not exists.
     *
     * @return OAuth2UserService
     */
    private OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
        return userRequest -> {
            UserRole defaultRole = UserRole.ROLE_NOT_SETUP;

            OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
            OAuth2User user = delegate.loadUser(userRequest);

            String email = user.getAttribute("email");
            String picture = user.getAttribute("picture");

            if (email == null) {
                logger.error("Email not found in OAuth2 response {}", user.getAttributes());
                throw new OAuth2AuthenticationException("Email not found in OAuth2 response");
            }

            FullUserEntity userEntity = userService.getUserByEmail(email).orElseGet(
                    () -> {
                        URL profileImageUrl = null;
                        if (picture != null) {
                            try {
                                URI uri = new URI(picture);
                                profileImageUrl = uri.toURL();
                            } catch (URISyntaxException | MalformedURLException e) {
                                logger.error("Failed to create URL from profile image URL, ignoring", e);
                            }
                        }

                        UserInitRequest userInitRequest = UserInitRequest.builder()
                                .email(email)
                                .profileImageUrl(profileImageUrl)
                                .build();

                        return userService.createUser(userInitRequest, defaultRole);
                    }
            );

            Map<String, Object> attributes = new HashMap<>(user.getAttributes());
            attributes.put("entity", userEntity);

            var authorities = List.of(new SimpleGrantedAuthority(userEntity.getRole().name()));
            return new DefaultOAuth2User(authorities, attributes, "email");
        };
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("*");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
