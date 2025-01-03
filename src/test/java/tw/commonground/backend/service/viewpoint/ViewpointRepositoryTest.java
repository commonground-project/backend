package tw.commonground.backend.service.viewpoint;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;
import tw.commonground.backend.exception.EntityNotFoundException;
import tw.commonground.backend.service.viewpoint.entity.Reaction;
import tw.commonground.backend.service.viewpoint.entity.ViewpointEntity;
import tw.commonground.backend.service.viewpoint.entity.ViewpointRepository;

import java.util.UUID;

@DataJpaTest
public class ViewpointRepositoryTest {

    @Autowired
    private ViewpointRepository viewpointRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @Transactional
    void testUpdateReactionCount() {
        ViewpointEntity viewpointEntity = new ViewpointEntity();
        viewpointEntity.setTitle("Test viewpoint");
        viewpointRepository.save(viewpointEntity);
        UUID viewpointId = viewpointEntity.getId(); // Get the generated UUID

        viewpointRepository.updateReactionCount(viewpointId, Reaction.LIKE, 1);
        entityManager.clear();
        ViewpointEntity updatedViewpoint = viewpointRepository.findById(viewpointId).orElseThrow(
                () -> new EntityNotFoundException("Viewpoint", "id", viewpointId.toString())
        );
        assertEquals(1, updatedViewpoint.getLikeCount());
    }
}
