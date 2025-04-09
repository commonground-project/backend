package tw.commonground.backend.service.internal.similarity.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IssueSimilarityRepository
        extends JpaRepository<IssueSimilarityEntity, IssueSimilarityKey> {

    List<IssueSimilarityEntity> findByUserId(Long userId);

    @Modifying
    @Query(value = "INSERT INTO issue_similarity_entity (user_id, issue_id, similarity) "
            + "VALUES (:#{#id.userId}, :#{#id.issueId}, :similarity)", nativeQuery = true)
    void insertSimilarityById(IssueSimilarityKey id, Double similarity);

    @Modifying
    @Query(value = "UPDATE issue_similarity_entity SET similarity = :similarity "
            + "WHERE user_id = :#{#id.userId} AND issue_id = :#{#id.issueId}", nativeQuery = true)
    void updateSimilarityById(IssueSimilarityKey id, Double similarity);
}
