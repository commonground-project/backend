package tw.commonground.backend.service.user.entity;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.email = :email, u.role = :role WHERE u.id = :id")
    void updateById(@Param("id") Long id, @Param("email") String email, @Param("role") String role);


    Optional<UserEntity> findByEmail(String email);
    SimpleUserEntity findIdByEmail(String email);

    // Method using projection to get ID by email
    default Long getIdByEmail(String email) {
        SimpleUserEntity projection = findIdByEmail(email);
        return projection != null ? projection.getId() : null;
    }
}

