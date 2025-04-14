package tw.commonground.backend.service.reference;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReferenceRepository extends JpaRepository<ReferenceEntity, UUID> {

    Optional<ReferenceEntity> findByUrl(String reference);
}
