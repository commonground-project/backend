package tw.commonground.backend.service.jwt.entity;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends CrudRepository<RefreshTokenEntity, UUID> {
    Optional<RefreshTokenProjection> findByIdAndIsActiveAndExpirationTimeAfter(UUID id,
                                                                               boolean isActive,
                                                                               long expirationTime);

    @Modifying
    @Transactional
    @Query("UPDATE RefreshTokenEntity SET isActive = false WHERE id = :id")
    void inactivateById(UUID id);

    @Modifying
    @Transactional
    void deleteAllByExpirationTimeBefore(long expirationTime);
}
