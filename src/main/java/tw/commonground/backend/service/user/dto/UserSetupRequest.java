package tw.commonground.backend.service.user.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tw.commonground.backend.service.user.entity.UserGender;
import tw.commonground.backend.service.user.entity.UserOccupation;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@SuppressWarnings("MagicNumber")
public class UserSetupRequest {

    @Size(min = 4, max = 15, message = "Username must be between 4 and 15 characters long")
    @Pattern(regexp = "^\\w+$", message = "Username can only contain letters, numbers, and underscores")
    @NotBlank(message = "Username is required")
    private String username;

    @Size(min = 1, max = 20, message = "Nickname must be between 1 and 20 characters long")
    @NotBlank(message = "Nickname is required")
    private String nickname;

    private UserOccupation occupation;

    private UserGender gender;

    private LocalDate birthdate;
}
