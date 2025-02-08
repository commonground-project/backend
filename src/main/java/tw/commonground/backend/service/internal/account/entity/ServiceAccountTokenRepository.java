package tw.commonground.backend.service.internal.account.entity;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ServiceAccountTokenRepository extends JpaRepository<ServiceAccountTokenEntity, String> {
}
