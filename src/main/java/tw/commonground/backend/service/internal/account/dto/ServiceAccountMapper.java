package tw.commonground.backend.service.internal.account.dto;

import tw.commonground.backend.service.internal.account.TokenUserDetails;
import tw.commonground.backend.service.internal.account.entity.ServiceAccountEntity;

import java.util.List;

public final class ServiceAccountMapper {
    private ServiceAccountMapper() {
        // hide the constructor
    }

    public static List<ServiceAccountResponse> toResponses(List<ServiceAccountEntity> entities) {
        return entities.stream()
                .map(ServiceAccountMapper::toResponse)
                .toList();
    }

    public static ServiceAccountResponse toResponse(ServiceAccountEntity entity) {
        return new ServiceAccountResponse(
                entity.getId(),
                entity.getServiceName(),
                entity.getRole()
        );
    }

    public static ServiceAccountResponse toResponse(TokenUserDetails user) {
        return new ServiceAccountResponse(
                user.getUuid(),
                user.getUsername(),
                user.getRole()
        );
    }

    public static CreatedServiceAccountResponse toCreatedResponse(ServiceAccountEntity entity) {
        return new CreatedServiceAccountResponse(
                entity.getId(),
                entity.getServiceName(),
                entity.getRole(),
                entity.getToken()
        );
    }
}
