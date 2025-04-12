package tw.commonground.backend.service.subscription;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tw.commonground.backend.service.user.entity.FullUserEntity;
import tw.commonground.backend.shared.tracing.Traced;

@Traced
@RequestMapping("/api/subscription")
@RestController
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PreAuthorize("hasAnyRole('USER')")
    @PostMapping("/subscribe")
    public void saveSubscription(@AuthenticationPrincipal FullUserEntity user,
                                 @RequestBody SubscriptionRequest request) {
        subscriptionService.saveSubscription(request, user);
    }

    @PreAuthorize("hasAnyRole('USER')")
    @DeleteMapping("/unsubscribe")
    public void removeSubscription(@AuthenticationPrincipal FullUserEntity user,
                                        @RequestBody UnsubscriptionRequest request) {
        subscriptionService.removeSubscription(request, user);
    }
}
