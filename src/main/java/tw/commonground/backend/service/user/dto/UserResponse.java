package tw.commonground.backend.service.user.dto;


import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.Setter;
import tw.commonground.backend.service.user.entity.UserEntity;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class UserResponse {
    // only response data in API spec
    private String username;
    private String nickname;
    private String email;
    private byte[] profileImage;
    private String role;

    public UserResponse(String username, String nickname, String email, byte[] profileImage, String role) {
        this.username = username;
        this.nickname = nickname;
        this.email = email;
        this.profileImage = profileImage;
        this.role = role;
    }

    public static List<UserResponse> fromEntities(List<UserEntity> userEntities) {
        // try to let all userEntities in the list convert to UserResponse and return them
        return userEntities.stream()
                .map(userEntity -> new UserResponse(
                        userEntity.getUsername(),
                        userEntity.getNickname(),
                        userEntity.getEmail(),
                        userEntity.getProfileImage(),
                        userEntity.getRole()
                ))
                .collect(Collectors.toList());
    }
}
