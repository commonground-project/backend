package tw.commonground.backend.service.newcontent.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface NewcontentRepository extends JpaRepository<NewcontentEntity, NewcontentKey> {
    Optional<NewcontentEntity> findByIdUserIdAndIdObjectIdAndIdObjectType(
            Long userId, UUID objectId, NewcontentObjectType objectType);
}