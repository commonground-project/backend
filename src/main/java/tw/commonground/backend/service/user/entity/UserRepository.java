package tw.commonground.backend.service.user.entity;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import tw.commonground.backend.security.UserRole;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends CrudRepository<UserEntity, Long> {

    Optional<FullUserEntity> findUserEntityByEmail(String email);

    Optional<FullUserEntity> findUserEntityByUsername(String username);

    Optional<FullUserEntity> findUserEntityById(Long id);

    Optional<FullUserEntity> findIdByEmail(String email);

    Optional<ProfileEntity> findProfileEntityById(Long id);

    Optional<ProfileEntity> findProfileEntityByUsername(String username);

    Optional<ProfileEntity> findProfileEntityByEmail(String email);

    Optional<UserEntity> getUserEntityByUsername(String username);

    SimpleUserEntity findByEmail(String email);

    boolean existsByUsername(String username);

    @Query("SELECT u.id FROM UserEntity u WHERE u.uuid = ?1")
    Long getIdByUid(UUID uid);

    @Query("SELECT u.uuid FROM UserEntity u WHERE u.id = ?1")
    UUID getUidById(Long id);

    @Query("Select u from UserEntity u where u.username in :usernames")
    List<UserEntity> getUsersByUsername(@Param("usernames") List<String> usernames);

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.username = ?2, u.nickname = ?3 WHERE u.id = ?1")
    void setupUserById(Long id, String username, String nickname);

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.role = ?2 WHERE u.id = ?1")
    void setupUserRoleById(Long id, UserRole role);

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.birthdate = ?2, u.occupation = ?3, u.gender = ?4 WHERE u.id = ?1")
    void setupUserInformationById(Long id, LocalDate birthdate, UserOccupation occupation, UserGender gender);
}
