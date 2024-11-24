package tw.commonground.backend.service.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.net.URL;
import java.util.Optional;

@Data
@Builder
public class UserInitRequest {

    @NotBlank
    private String email;

    @Nullable
    private URL profileImageUrl;

    public Optional<URL> getProfileImageUrl() {
        return Optional.ofNullable(profileImageUrl);
    }
}
