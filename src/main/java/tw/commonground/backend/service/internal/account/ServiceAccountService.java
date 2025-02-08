package tw.commonground.backend.service.internal.account;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import tw.commonground.backend.service.internal.account.entity.ServiceAccountRole;
import tw.commonground.backend.service.internal.account.entity.ServiceAccountTokenEntity;
import tw.commonground.backend.service.internal.account.entity.ServiceAccountTokenRepository;

@Service
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
             ServiceAccountTokenEntity serviceAccountTokenEntity = ServiceAccountTokenEntity.builder()
                     .token(defaultToken)
                     .serviceName("default")
                     .role(ServiceAccountRole.SERVICE_ACCOUNT_ROLE_READ)
                     .build();
             serviceAccountTokenRepository.save(serviceAccountTokenEntity);
        }
    }
}
