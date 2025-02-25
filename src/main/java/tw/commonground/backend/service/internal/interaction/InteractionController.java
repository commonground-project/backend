package tw.commonground.backend.service.internal.interaction;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tw.commonground.backend.service.internal.interaction.dto.InteractionMapper;
import tw.commonground.backend.service.internal.interaction.dto.InteractionResponse;
import tw.commonground.backend.service.internal.interaction.entity.InteractionEntity;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/internal/users/interaction")
public class InteractionController {

    private final InteractionService interactionService;

    public InteractionController(InteractionService interactionService) {
        this.interactionService = interactionService;
    }

    @GetMapping
    public ResponseEntity<List<InteractionResponse>> getProfiles() {
        List<InteractionEntity> interactionEntities = interactionService.getAllInteractions();

        return ResponseEntity.ok(interactionEntities.stream()
                .map(InteractionMapper::toInteractionResponse)
                .collect(Collectors.toList()));
    }
}
