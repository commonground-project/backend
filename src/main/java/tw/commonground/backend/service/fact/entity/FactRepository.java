package tw.commonground.backend.service.fact.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FactRepository extends JpaRepository<FactEntity, UUID> {
}
