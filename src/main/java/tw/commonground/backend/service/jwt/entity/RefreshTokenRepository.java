package tw.commonground.backend.service.jwt.entity;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends CrudRepository<RefreshTokenEntity, UUID> {
    Optional<RefreshTokenProjection> findByIdAndIsActiveAndExpirationTimeBefore
            (UUID id, boolean isActive, long expirationTime);
}
