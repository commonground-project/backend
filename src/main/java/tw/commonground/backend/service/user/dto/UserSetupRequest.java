package tw.commonground.backend.service.user.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSetupRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String nickname;
}
