package tw.commonground.backend.service.user.dto;

import tw.commonground.backend.service.user.entity.FullUserEntity;
import tw.commonground.backend.service.user.entity.UserEntity;

import java.util.List;

public class UserMapper {
    private UserMapper() {
        // hide the constructor
    }

    public static UserResponse toResponse(FullUserEntity entity) {
        return new UserResponse(
                entity.getUsername(),
                entity.getNickname(),
                entity.getEmail(),
                entity.getRole().name()
        );
    }

    public static List<UserResponse> toResponses(List<UserEntity> userEntities) {
        // try to let all userEntities in the list convert to UserResponse and return them
        return userEntities.stream()
                .map(userEntity -> new UserResponse(
                        userEntity.getUsername(),
                        userEntity.getNickname(),
                        userEntity.getEmail(),
                        userEntity.getRole().name()
                ))
                .toList();
    }
}
