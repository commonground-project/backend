package tw.commonground.backend.service.user.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;
import tw.commonground.backend.security.UserRole;

@Getter
@Setter
@SuppressWarnings("MagicNumber")
public class UpdateUserRequest {

    @Nullable
    @Size(min = 4, max = 15, message = "Username must be between 4 and 15 characters long")
    @Pattern(regexp = "^\\w+$", message = "Username can only contain letters, numbers, and underscores")
    private String username;

    @Nullable
    @Size(min = 1, max = 20, message = "Nickname must be between 1 and 20 characters long")
    private String nickname;

    @Nullable
    private UserRole role;
}
