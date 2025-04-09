package tw.commonground.backend.service.internal.similarity.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ViewpointSimilarityRepository
        extends JpaRepository<ViewpointSimilarityEntity, ViewpointSimilarityKey> {

    List<ViewpointSimilarityEntity> findByUserId(Long userId);

    @Modifying
    @Query(value = "INSERT INTO viewpoint_similarity_entity (user_id, viewpoint_id, similarity) "
            + "VALUES (:#{#id.userId}, :#{#id.viewpointId}, :similarity)", nativeQuery = true)
    void insertSimilarityById(ViewpointSimilarityKey id, Double similarity);

    @Modifying
    @Query(value = "UPDATE viewpoint_similarity_entity SET similarity = :similarity "
            + "WHERE user_id = :#{#id.userId} AND viewpoint_id = :#{#id.viewpointId}", nativeQuery = true)
    void updateSimilarityById(ViewpointSimilarityKey id, Double similarity);
}
