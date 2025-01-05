package tw.commonground.backend.service.viewpoint;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;
import tw.commonground.backend.exception.ValidationException;
import tw.commonground.backend.service.user.entity.UserRepository;
import tw.commonground.backend.service.viewpoint.entity.Reaction;
import tw.commonground.backend.service.viewpoint.entity.ViewpointReactionEntity;
import tw.commonground.backend.service.viewpoint.entity.ViewpointReactionKey;
import tw.commonground.backend.service.viewpoint.entity.ViewpointRepository;
import tw.commonground.backend.service.viewpoint.entity.ViewpointReactionRepository;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("MethodName")
public class ViewpointServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ViewpointRepository viewpointRepository;

    @Mock
    private ViewpointReactionRepository viewpointReactionRepository;

    @InjectMocks
    private ViewpointService viewpointService;

    @Test
    @Transactional
    void testReactToViewpointNewReaction() {
        UUID viewpointId = UUID.randomUUID();
        Long userId = 1L;
        Reaction reaction = Reaction.LIKE;

        when(userRepository.existsById(userId)).thenReturn(true);
        when(viewpointRepository.existsById(viewpointId)).thenReturn(true);
        when(viewpointReactionRepository.findById(any(ViewpointReactionKey.class))).thenReturn(Optional.empty());

        ViewpointReactionEntity result = viewpointService.reactToViewpoint(userId, viewpointId, reaction);

        verify(viewpointReactionRepository, times(1))
                .insertReaction(any(ViewpointReactionKey.class), eq(reaction.name()));
        verify(viewpointRepository, times(1)).updateReactionCount(viewpointId, reaction, 1);
        assertEquals(reaction, result.getReaction());
    }


    @ParameterizedTest
    @MethodSource("provideInvalidUserId")
    @Transactional
    void testReactToViewpoint_InvalidUser(Long userId) {
        UUID viewpointId = UUID.randomUUID();
        Reaction reaction = Reaction.LIKE;

        when(userRepository.existsById(userId)).thenReturn(userId != null && userId > 0);
        when(viewpointRepository.existsById(viewpointId)).thenReturn(true);

        assertThatThrownBy(() -> viewpointService.reactToViewpoint(userId, viewpointId, reaction))
                .isInstanceOf(ValidationException.class);
    }

    private static Stream<Object[]> provideInvalidUserId() {
        return Stream.of(
                new Object[]{null}, // userId is null
                new Object[]{-1L}   // userId not found
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidViewpointId")
    @Transactional
    void testReactToViewpoint_InvalidViewpoint(UUID viewpointId) {
        Long userId = 1L;
        Reaction reaction = Reaction.LIKE;

//        when(userRepository.existsById(userId)).thenReturn(true);
        when(viewpointRepository.existsById(viewpointId)).thenReturn(viewpointId != null);

        assertThatThrownBy(() -> viewpointService.reactToViewpoint(userId, viewpointId, reaction))
                .isInstanceOf(ValidationException.class);
    }

    private static Stream<Object[]> provideInvalidViewpointId() {
        return Stream.of(
                new Object[]{null},             // viewpointId is null
                new Object[]{UUID.randomUUID()} // viewpointId not found
        );
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
        when(viewpointReactionRepository.findById(any(ViewpointReactionKey.class)))
                .thenReturn(Optional.of(existingReaction));

        ViewpointReactionEntity result = viewpointService.reactToViewpoint(userId, viewpointId, newReaction);

        verify(viewpointReactionRepository, times(1)).updateReaction(reactionKey, newReaction.name());
        // if oldReaction != NONE
        verify(viewpointRepository, times(1)).updateReactionCount(viewpointId, oldReaction, -1);
        verify(viewpointRepository, times(1)).updateReactionCount(viewpointId, newReaction, 1);

        assertEquals(newReaction, result.getReaction());
    }

//    @Test
//    @Transactional
//    void testReactToViewpoint_UserNotFound() {
//        UUID viewpointId = UUID.randomUUID();
//        Long userId = null;
//        Reaction reaction = Reaction.LIKE;
//
//        when(userRepository.existsById(userId)).thenReturn(false);
//        when(viewpointRepository.existsById(viewpointId)).thenReturn(true);
//
//        assertThatThrownBy(() -> viewpointService.reactToViewpoint(userId, viewpointId, reaction))
//                .isInstanceOf(ValidationException.class);
//    }

    // Null and Not found should be seperated to two tests?


//    @Test
//    void testReactToViewpoint_ViewpointNotFound() {
//        Long userId = 1L;
//        UUID viewpointId = UUID.randomUUID();
//        Reaction reaction = Reaction.LIKE;
//
//        when(viewpointRepository.existsById(viewpointId)).thenReturn(false);
//        assertThatThrownBy(() -> viewpointService.reactToViewpoint(userId, viewpointId, reaction))
//                .isInstanceOf(ValidationException.class);
//    }


}
