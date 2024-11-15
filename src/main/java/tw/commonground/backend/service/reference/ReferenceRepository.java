package tw.commonground.backend.service.reference;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ReferenceRepository extends JpaRepository<ReferenceEntity, Long> {

    List<ReferenceEntity> findAllByUrlIn(Set<String> reference);
}
