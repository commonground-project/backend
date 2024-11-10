package tw.commonground.backend.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import tw.commonground.backend.service.user.entity.UserEntity;
import tw.commonground.backend.service.user.exception.EmailNotFoundException;

import java.util.List;


@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/api/user")
    public List<UserEntity> getUser() {
        return userService.getUser();
    }




}
