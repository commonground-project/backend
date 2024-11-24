package tw.commonground.backend.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

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

    public SecurityConfiguration(UserService userService) {
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/setup/**").hasRole("SETUP_REQUIRED")
                        .requestMatchers("/api/**").authenticated()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(this.oauth2UserService())
                        )
                        .authorizationEndpoint(authorization -> authorization
                                .baseUri("/api/oauth2")
                        )
                )
                .logout(logout -> logout
                        .logoutUrl("/api/logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
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

            var authorities = List.of(new SimpleGrantedAuthority(userEntity.getRole().name()));
            return new DefaultOAuth2User(authorities, user.getAttributes(), "email");
        };
    }
}
