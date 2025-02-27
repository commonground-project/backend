package tw.commonground.backend.service.internal.account;

import lombok.ToString;
import tw.commonground.backend.service.internal.account.entity.ServiceAccountEntity;
import tw.commonground.backend.service.user.entity.FullUserEntity;
import tw.commonground.backend.security.UserRole;

import java.util.UUID;

@ToString
public class TokenUserDetails implements FullUserEntity {

    private final ServiceAccountEntity serviceAccountEntity;

    public TokenUserDetails(ServiceAccountEntity serviceAccountEntity) {
        this.serviceAccountEntity = serviceAccountEntity;
    }

    @Override
    public Long getId() {
        // Todo: Implement the methods of the FullUserEntity interface
        return null;
    }

    @Override
    public UUID getUuid() {
        return serviceAccountEntity.getId();
    }

    @Override
    public String getUsername() {
        return serviceAccountEntity.getServiceName();
    }

    @Override
    public String getEmail() {
        return serviceAccountEntity.getServiceName() + "@serviceaccount.commonground.tw";
    }

    @Override
    public String getNickname() {
        return serviceAccountEntity.getServiceName();
    }

    @Override
    public UserRole getRole() {
        return serviceAccountEntity.getRole();
    }
}
