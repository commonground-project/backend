package tw.commonground.backend.service.user.entity;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, Long> {

    @Query("SELECT u FROM UserEntity u WHERE u.email = ?1")
    Optional<UserEntity> findAllByEmail(String email);

}
