package tw.commonground.backend.service.user;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import tw.commonground.backend.service.user.dto.UserResponse;
import tw.commonground.backend.service.user.dto.UserSetupRequest;
import tw.commonground.backend.service.user.entity.UserEntity;
import tw.commonground.backend.service.user.entity.UserRepository;
import tw.commonground.backend.service.user.exception.EmailNotFoundException;
import tw.commonground.backend.service.user.exception.IdNotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public String createUser(String email, String profileImageUrl) {
        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setProfileImageUrl(profileImageUrl);
        user.setRole("ROLE_SETUP_REQUIRED");
        userRepository.save(user);
        roleSynchronize(user);
        return user.getId().toString(); // return the user's ID
    }

    public UserResponse loadUserByEmail(String email) throws EmailNotFoundException {
        Optional<UserEntity> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            UserEntity userEntity = user.get();
            return new UserResponse(
                    userEntity.getUsername(),
                    userEntity.getNickname(),
                    userEntity.getEmail(),
                    userEntity.getProfileImage(),
                    userEntity.getRole()
            );
        } else {
            throw new EmailNotFoundException(email);
        }
    }

    public UserResponse loadUserById(Long id) throws IdNotFoundException {
        Optional<UserEntity> user = userRepository.findById(id);
        if (user.isPresent()) {
            UserEntity userEntity = user.get();
            return new UserResponse(
                    userEntity.getUsername(),
                    userEntity.getNickname(),
                    userEntity.getEmail(),
                    userEntity.getProfileImage(),
                    userEntity.getRole()
            );
        } else {
            throw new IdNotFoundException(id);
        }
    }

    public void roleSynchronize(UserEntity user) {
        System.out.println("Role synchronized");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<GrantedAuthority> updatedAuthorities = Collections.singletonList(new SimpleGrantedAuthority(user.getRole()));

        if (auth == null) { // initial authentication
            Authentication newAuth = new UsernamePasswordAuthenticationToken(
                    user.getEmail(),  // user's email as principal
                    null,             // Google OAuth2 no password
                    Collections.singletonList(new SimpleGrantedAuthority(user.getRole()))
            );
            SecurityContextHolder.getContext().setAuthentication(newAuth);
            System.out.println("Initial authentication set for first login.");
            return;
        }

        Authentication newAuth = new UsernamePasswordAuthenticationToken(   // use oauth2
                auth.getPrincipal(),
                auth.getCredentials(),
                updatedAuthorities
        );
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }



    @Secured("ROLE_ADMIN")
    public List<UserEntity> getUser() {
        return (List<UserEntity>) userRepository.findAll();
    }

    public boolean isEmailRegistered(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public UserEntity getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public String getUserIdByEmail(String email) {
        return userRepository.findIdByEmail(email).getId().toString();
    }

    public String completeSetup(UserSetupRequest setupRequest, OAuth2User principal) {
        String email = principal.getAttribute("email");
        Optional<UserEntity> userEntityOptional = userRepository.findByEmail(email);

        if (userEntityOptional.isPresent()) {
            UserEntity user = userEntityOptional.get();
            user.setUsername(setupRequest.getUsername());
            user.setNickname(setupRequest.getNickname());
            user.setRole("ROLE_USER");
            userRepository.save(user);
            roleSynchronize(user);

            return "User setup completed successfully.";
        } else {
            throw new EmailNotFoundException(email);
        }
    }

    public UserEntity getMe(OAuth2User principal) {
        String email = principal.getAttribute("email");
        Optional<UserEntity> userEntityOptional = userRepository.findByEmail(email);

        if (userEntityOptional.isPresent()) {
            return userEntityOptional.get();
        } else {
            throw new EmailNotFoundException(email);
        }
    }
}
