package tw.commonground.backend.service.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.lang.Nullable;

import java.net.URL;
import java.util.Optional;

@Data
@Builder
@ToString
public class UserInitRequest {

    @Email(message = "Invalid email address")
    private String email;

    @Nullable
    @org.hibernate.validator.constraints.URL(message = "Invalid URL")
    private URL profileImageUrl;

    public Optional<URL> getProfileImageUrl() {
        return Optional.ofNullable(profileImageUrl);
    }
}
