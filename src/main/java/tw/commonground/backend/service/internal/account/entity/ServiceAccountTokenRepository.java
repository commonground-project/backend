package tw.commonground.backend.service.internal.account.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;


public interface ServiceAccountTokenRepository extends JpaRepository<ServiceAccountEntity, UUID> {
    Optional<ServiceAccountEntity> findByToken(String token);

    Optional<ServiceAccountEntity> findByServiceName(String serviceName);
}
