package tw.commonground.backend.service.viewpoint.entity;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import tw.commonground.backend.shared.entity.Reaction;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ViewpointReactionRepository extends JpaRepository<ViewpointReactionEntity, ViewpointReactionKey> {

    @Cacheable("viewpointReaction")
    @Query("SELECT v.reaction FROM ViewpointReactionEntity v WHERE v.id = :id")
    Optional<Reaction> findReactionById(ViewpointReactionKey id);

    @Cacheable("viewpointReaction")
    @Query("SELECT v FROM ViewpointReactionEntity v WHERE v.user.id = :userId AND v.viewpoint.id IN :viewpointIds")
    List<ViewpointReactionEntity> findReactionsByUserIdAndViewpointIds(Long userId, List<UUID> viewpointIds);

    @Caching(evict = {
            @CacheEvict(value = "viewpoint", allEntries = true),
            @CacheEvict(value = "viewpointReaction", allEntries = true)
    })
    @Modifying
    @Query(value = "INSERT INTO viewpoint_reaction_entity (viewpoint_id, user_id, reaction) "
            + "VALUES (:#{#id.viewpointId}, :#{#id.userId}, :reaction)", nativeQuery = true)
    void insertReaction(ViewpointReactionKey id, String reaction);

    @Caching(evict = {
            @CacheEvict(value = "viewpoint", allEntries = true),
            @CacheEvict(value = "viewpointReaction", allEntries = true)
    })
    @Modifying
    @Query(value = "UPDATE viewpoint_reaction_entity SET reaction = :reaction "
            + "WHERE viewpoint_id = :#{#id.viewpointId} AND user_id = :#{#id.userId}", nativeQuery = true)
    void updateReaction(ViewpointReactionKey id, String reaction);
}
