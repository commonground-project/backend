package tw.commonground.backend.service.read.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadRepository extends JpaRepository<ReadEntity, ReadKey> {
    Optional<ReadEntity> findByIdUserIdAndIdObjectIdAndIdObjectType(
            Long userId, UUID objectId, ReadObjectType objectType);


    @Query("SELECT r FROM ReadEntity r WHERE r.id.objectType = :objectType AND r.timestamp < :timestamp")
    List<ReadEntity> findByObjectTypeAndTimestampBefore(@Param("objectType") ReadObjectType objectType,
                                                        @Param("timestamp") LocalDateTime timestamp);
}
