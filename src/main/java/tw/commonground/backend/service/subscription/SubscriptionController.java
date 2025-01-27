package tw.commonground.backend.service.subscription;

import net.minidev.json.JSONObject;
import org.jose4j.lang.JoseException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tw.commonground.backend.service.user.entity.FullUserEntity;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.ExecutionException;

@RestController
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping("/api/subscription/subscribe")
    public void saveSubscription(@AuthenticationPrincipal FullUserEntity user,
                                 @RequestBody SubscriptionRequest request) {
        subscriptionService.saveSubscription(request, user);
    }

    @PostMapping("/api/subscription/unsubscribe")
    public void unsubscribeSubscription(@AuthenticationPrincipal FullUserEntity user,
                                        @RequestBody SubscriptionRequest request) {
        subscriptionService.removeSubscription(request);
    }

    @GetMapping("/api/subscription/sendNotification")
    public void sendNotification()
            throws JoseException, GeneralSecurityException, IOException, ExecutionException, InterruptedException {
        JSONObject json = new JSONObject();
        json.put("title", "test");
        subscriptionService.sendNotification(json.toString());
    }

}
