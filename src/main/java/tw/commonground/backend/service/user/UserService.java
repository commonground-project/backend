package tw.commonground.backend.service.user;

import jakarta.validation.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tw.commonground.backend.service.user.entity.UserEntity;
import tw.commonground.backend.service.user.entity.UserRepository;
import tw.commonground.backend.service.user.exception.EmailNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public void createUser(String name) {
        UserEntity user = new UserEntity();
        user.setUsername(name);
        userRepository.save(user);
    }

    public UserDetails loadUserByEmail(String email) throws EmailNotFoundException {
        Optional<UserEntity> user = userRepository.findAllByEmail(email);

        if (user.isPresent()) {
            UserEntity userObj = user.get();
            return User.builder()
                    .username(userObj.getUsername())
                    .build();
        } else {
            throw new EmailNotFoundException(email); // 使用自定義的 EmailNotFoundException
        }
    }

    @Secured("ROLE_ADMIN")
    public List<UserEntity> getUser() {
        return (List<UserEntity>) userRepository.findAll();
    }

    public boolean isEmailRegistered(String email) {
        return userRepository.findAllByEmail(email).isPresent();
    }

    public void createUserWithSetupRole(String email, String roleSetupRequired) {
        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setRole(roleSetupRequired);
        userRepository.save(user);
    }
}
