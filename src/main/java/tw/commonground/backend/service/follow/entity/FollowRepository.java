package tw.commonground.backend.service.follow.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tw.commonground.backend.shared.entity.RelatedObject;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface FollowRepository extends JpaRepository<FollowEntity, FollowKey> {

    @Query("select i.follow from FollowEntity i where i.id = :id")
    Optional<Boolean> findFollowById(FollowKey id);

    @Query("select i.user.id from FollowEntity i where i.id.objectId = :objectId and i.id.objectType = :objectType and i.follow = true")
    Optional<List<Long>> findUsersIdByObjectIdAndFollowTrue(UUID objectId, RelatedObject objectType);

//    @Modifying
//    @Query(value = "insert into follow_entity (user_id, object_id, object_type, follow, updated_at) "
//            + "values (:#{#id.userId}, :#{#id.objectId}, :#{#id.objectType}, :follow, current_timestamp)", nativeQuery = true)
//    void insertFollowById(FollowKey id, Boolean follow);
//
//    @Modifying
//    @Query(value = "update follow_entity set follow = :follow, updated_at = current_timestamp "
//            + "where user_id = :#{#id.userId} and object_id = :#{#id.objectId} and object_type = :#{#id.objectType}", nativeQuery = true)
//    void updateFollowById(FollowKey id, Boolean follow);
}
