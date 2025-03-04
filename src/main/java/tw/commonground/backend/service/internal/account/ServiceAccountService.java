package tw.commonground.backend.service.internal.account;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import tw.commonground.backend.service.internal.account.entity.ServiceAccountEntity;
import tw.commonground.backend.service.internal.account.entity.ServiceAccountTokenRepository;
import tw.commonground.backend.security.UserRole;
import tw.commonground.backend.shared.tracing.Traced;

import java.util.List;
import java.util.UUID;

@Traced
@Service
@CacheConfig(cacheNames = "serviceAccount")
public class ServiceAccountService {

    @Value("${application.service.account.default.token:}")
    private String defaultToken;

    private final ServiceAccountTokenRepository serviceAccountTokenRepository;

    public ServiceAccountService(ServiceAccountTokenRepository serviceAccountTokenRepository) {
        this.serviceAccountTokenRepository = serviceAccountTokenRepository;
    }

    @EventListener(classes = {ContextRefreshedEvent.class})
    public void onApplicationContextRefreshed() {
        if (serviceAccountTokenRepository.count() == 0 && defaultToken != null && !defaultToken.isEmpty()) {
            // Create default token
            ServiceAccountEntity serviceAccountEntity = ServiceAccountEntity.builder()
                    .token(defaultToken)
                    .serviceName("default")
                    .role(UserRole.ROLE_SERVICE_ACCOUNT_READ)
                    .build();
            serviceAccountTokenRepository.save(serviceAccountEntity);
        }
    }

    @CacheEvict(allEntries = true)
    public ServiceAccountEntity createServiceAccount(String serviceName, UserRole role, String token) {
        if (!role.name().startsWith("ROLE_SERVICE_ACCOUNT")) {
            throw new IllegalArgumentException("Role must be a service account role");
        }

        ServiceAccountEntity serviceAccountEntity = ServiceAccountEntity.builder()
                .token(token)
                .serviceName(serviceName)
                .role(role)
                .build();
        return serviceAccountTokenRepository.save(serviceAccountEntity);
    }

    @Cacheable
    public List<ServiceAccountEntity> getServiceAccounts() {
        return serviceAccountTokenRepository.findAll();
    }

    @CacheEvict(allEntries = true)
    public void deleteServiceAccount(UUID serviceId) {
        serviceAccountTokenRepository.deleteById(serviceId);
    }

    @Cacheable
    public TokenUserDetails authenticate(String token) {
        return serviceAccountTokenRepository.findByToken(token)
                .map(TokenUserDetails::new)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));
    }
}
