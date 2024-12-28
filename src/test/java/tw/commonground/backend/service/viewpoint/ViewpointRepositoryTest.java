package tw.commonground.backend.service.viewpoint;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;
import tw.commonground.backend.service.viewpoint.entity.Reaction;
import tw.commonground.backend.service.viewpoint.entity.ViewpointEntity;
import tw.commonground.backend.service.viewpoint.entity.ViewpointRepository;

import java.util.UUID;

@DataJpaTest
public class ViewpointRepositoryTest {

    @Autowired
    private ViewpointRepository viewpointRepository;

    @Test
    @Transactional
    void testUpdateReactionCount() {

        UUID viewpointId = UUID.randomUUID();
        ViewpointEntity viewpointEntity = new ViewpointEntity();
        viewpointEntity.setId(viewpointId);
        viewpointRepository.save(viewpointEntity);

        viewpointRepository.updateReactionCount(viewpointId, Reaction.LIKE, 1);

        ViewpointEntity updatedViewpoint = viewpointRepository.findById(viewpointId).orElseThrow();
        assertEquals(1, updatedViewpoint.getLikeCount());
    }
}
