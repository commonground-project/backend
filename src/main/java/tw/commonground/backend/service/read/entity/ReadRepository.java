package tw.commonground.backend.service.read.entity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReadRepository extends JpaRepository<ReadEntity, ReadKey> {
}