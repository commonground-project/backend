package tw.commonground.backend.service.user.entity;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, Long> {

    Optional<FullUserEntity> findUserEntityByEmail(String email);

    Optional<FullUserEntity> findUserEntityByUsername(String username);

    Optional<FullUserEntity> findUserEntityById(Long id);

    Optional<FullUserEntity> findIdByEmail(String email);

    Optional<UserEntity> getUserEntityByUsername(String username);

    SimpleUserEntity findByEmail(String email);

    @Query("SELECT u.id FROM UserEntity u WHERE u.uuid = ?1")
    Long getIdByUid(String uid);

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.username = ?2, u.nickname = ?3, u.role = ?4 WHERE u.id = ?1")
    void setupUserById(Long id, String username, String nickname, UserRole role);


}

