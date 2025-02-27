package tw.commonground.backend.service.internal.interaction.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InteractionRepository extends JpaRepository<InteractionEntity, UUID> {
    List<InteractionEntity> findAllByOrderByTimestampDesc();
}
