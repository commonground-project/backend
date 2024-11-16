package tw.commonground.backend.service.user.entity;

import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);
    SimpleUserEntity findIdByEmail(String email);

    // Method using projection to get ID by email
    default Long getIdByEmail(String email) {
        SimpleUserEntity projection = findIdByEmail(email);
        return projection != null ? projection.getId() : null;
    }
}

