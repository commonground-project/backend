package tw.commonground.backend.service.user.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse {
    // only response data in API spec
    private String username;
    private String nickname;
    private String email;
    private String role;

    public UserResponse(String username, String nickname, String email, String role) {
        this.username = username;
        this.nickname = nickname;
        this.email = email;
        this.role = role;
    }
}
