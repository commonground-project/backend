package tw.commonground.backend.service.internal.account.entity;

import io.micrometer.context.NonNullApi;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface ServiceAccountTokenRepository extends JpaRepository<ServiceAccountEntity, UUID> {

    @Cacheable("serviceAccountToken")
    Optional<ServiceAccountEntity> findByToken(String token);

    @Cacheable("serviceAccountToken")
    List<ServiceAccountEntity> findAll();

    @CacheEvict(value = "serviceAccountToken", allEntries = true)
    void deleteById(UUID id);

    @CacheEvict(value = "serviceAccountToken", allEntries = true)
    ServiceAccountEntity save(ServiceAccountEntity serviceAccountEntity);

    Optional<ServiceAccountEntity> findByServiceName(String serviceName);
}
