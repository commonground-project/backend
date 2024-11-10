package tw.commonground.backend.service.user;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSetupRequest {
    private String username;
    private String nickname;
    private String email;
    private byte[] profileImage;

}
