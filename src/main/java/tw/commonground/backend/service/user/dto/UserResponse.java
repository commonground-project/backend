package tw.commonground.backend.service.user.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserResponse {
    private String username;
    private String nickname;
    private String email;
    private String role;

    public UserResponse(String username, String nickname, String email, String role) {
        this.username = username == null ? "" : username;
        this.nickname = nickname == null ? "" : nickname;
        this.email = email == null ? "" : email;
        this.role = role == null ? "" : role;
    }
}
