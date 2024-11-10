package tw.commonground.backend.service.user.entity;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.Set;

public interface UserRepository extends CrudRepository<UserEntity, Long> {
    @Query("SELECT u FROM UserEntity u WHERE u.username = ?1")
    Set<UserEntity> findAllByUsername(String username);

    @Query("SELECT u FROM UserEntity u WHERE u.email = ?1")
    Optional<UserEntity> findAllByEmail(String email);
}
