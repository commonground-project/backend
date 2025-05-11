package tw.commonground.backend.service.recommend.entity;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecommendRepository extends CrudRepository<RecommendEntity, String> {
    RecommendEntity findById(String id);

    void deleteById(String id);
}
