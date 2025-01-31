package tw.commonground.backend.service.subscription;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tw.commonground.backend.service.user.entity.FullUserEntity;

@RequestMapping("/api/subscription")
@RestController
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/subscribe")
    public void saveSubscription(@AuthenticationPrincipal FullUserEntity user,
                                 @RequestBody SubscriptionRequest request) {
        subscriptionService.saveSubscription(request, user);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/unsubscribe")
    public void unsubscribeSubscription(@AuthenticationPrincipal FullUserEntity user,
                                        @RequestBody SubscriptionRequest request) {
        subscriptionService.removeSubscription(request, user);
    }


}
