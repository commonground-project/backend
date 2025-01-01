package tw.commonground.backend.service.viewpoint;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;
import tw.commonground.backend.exception.EntityNotFoundException;
import tw.commonground.backend.service.viewpoint.entity.Reaction;
import tw.commonground.backend.service.viewpoint.entity.ViewpointReactionEntity;
import tw.commonground.backend.service.viewpoint.entity.ViewpointReactionKey;
import tw.commonground.backend.service.viewpoint.entity.ViewpointRepository;
import tw.commonground.backend.service.viewpoint.entity.ViewpointReactionRepository;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ViewpointServiceTest {

    @Mock
    private ViewpointRepository viewpointRepository;

    @Mock
    private ViewpointReactionRepository viewpointReactionRepository;

    @InjectMocks
    private ViewpointService viewpointService;


    @Test
    @Transactional
    void testReactToViewpoint_NewReaction() {
        UUID viewpointId = UUID.randomUUID();
        Long userId = 1L;
        Reaction reaction = Reaction.LIKE;

        when(viewpointRepository.existsById(viewpointId)).thenReturn(true);
        when(viewpointReactionRepository.findById(any(ViewpointReactionKey.class))).thenReturn(Optional.empty());

        ViewpointReactionEntity result = viewpointService.reactToViewpoint(userId, viewpointId, reaction);
        verify(viewpointReactionRepository, times(1)).insertReaction(any(ViewpointReactionKey.class), eq(reaction.name()));
        verify(viewpointRepository, times(1)).updateReactionCount(viewpointId, reaction, 1);
        assertEquals(reaction, result.getReaction());
    }

    @Test
    @Transactional
    void testReactToViewpoint_ExistingReaction() {
        UUID viewpointId = UUID.randomUUID();
        Long userId = 1L;
        Reaction oldReaction = Reaction.LIKE;
        Reaction newReaction = Reaction.DISLIKE;

        ViewpointReactionKey reactionKey = new ViewpointReactionKey(userId, viewpointId);
        ViewpointReactionEntity existingReaction = new ViewpointReactionEntity();
        existingReaction.setId(reactionKey);
        existingReaction.setReaction(oldReaction);

        when(viewpointRepository.existsById(viewpointId)).thenReturn(true);
        when(viewpointReactionRepository.findById(any(ViewpointReactionKey.class))).thenReturn(Optional.of(existingReaction));

        ViewpointReactionEntity result = viewpointService.reactToViewpoint(userId, viewpointId, newReaction);

        verify(viewpointReactionRepository, times(1)).updateReaction(reactionKey, newReaction.name());
        // if oldReaction != NONE
        verify(viewpointRepository, times(1)).updateReactionCount(viewpointId, oldReaction, -1);
        verify(viewpointRepository, times(1)).updateReactionCount(viewpointId, newReaction, 1);

        assertEquals(newReaction, result.getReaction());
    }

    @Test
    void testReactToViewpoint_ViewpointNotFound() {
        Long userId = 1L;
        UUID viewpointId = UUID.randomUUID();
        Reaction reaction = Reaction.LIKE;

        when(viewpointRepository.existsById(viewpointId)).thenReturn(false);
        assertThatThrownBy(() -> viewpointService.reactToViewpoint(userId, viewpointId, reaction))
                .isInstanceOf(EntityNotFoundException.class);
    }

}