package tw.commonground.backend.service.reference;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReferenceRepository extends JpaRepository<ReferenceEntity, Long> {

    Optional<ReferenceEntity> findByUrl(String reference);
}
