package tw.commonground.backend.service.user.entity;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, Long> {

    Optional<FullUserEntity> findUserEntityByEmail(String email);

    Optional<FullUserEntity> findUserEntityByUsername(String username);

    SimpleUserEntity findIdByEmail(String email);

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.username = ?2, u.nickname = ?3, u.role = ?4 WHERE u.id = ?1")
    void setupUserById(Long id, String username, String nickname, UserRole role);

    // Method using projection to get ID by email
    default Long getIdByEmail(String email) {
        SimpleUserEntity projection = findIdByEmail(email);
        return projection != null ? projection.getId() : null;
    }
}

