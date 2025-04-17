package tw.commonground.backend.service.read.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ReadRepository extends JpaRepository<ReadEntity, ReadKey> {
    Optional<ReadEntity> findByIdUserIdAndIdObjectIdAndIdObjectType(Long userId, UUID objectId, ReadObjectType objectType);
}