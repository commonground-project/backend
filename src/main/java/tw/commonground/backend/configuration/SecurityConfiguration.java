package tw.commonground.backend.configuration;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import tw.commonground.backend.service.user.UserService;

import java.io.IOException;
import java.util.*;

@EnableMethodSecurity(securedEnabled = true)
@EnableWebSecurity
@Configuration
public class SecurityConfiguration {

    @Autowired
    private UserService userService;


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
                );
        return http.build();
    }

    private OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
        return (userRequest) -> {
            OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
            OAuth2User user = delegate.loadUser(userRequest);

            String email = user.getAttribute("email");
            String profileImageUrl = user.getAttribute("picture");
            String id;

            ArrayList<GrantedAuthority> authorities = new ArrayList<>(user.getAuthorities());
            if (!userService.isEmailRegistered(email)) {
                authorities.add(new SimpleGrantedAuthority("ROLE_SETUP_REQUIRED"));
                id = userService.createUser(email, profileImageUrl);
                System.out.println("User created with ID: " + id);
            } else {
                id = userService.getUserIdByEmail(email);
            }
            return new DefaultOAuth2User(authorities, user.getAttributes(), "sub");
        };
    }


    @Component
    public static class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                            Authentication authentication) throws IOException, ServletException {
            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SETUP_REQUIRED"))) {
                setDefaultTargetUrl("/api/setup");
            } else {
                setDefaultTargetUrl("/api");
            }
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }
}