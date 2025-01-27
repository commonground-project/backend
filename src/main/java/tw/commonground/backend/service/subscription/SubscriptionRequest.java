package tw.commonground.backend.service.subscription;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubscriptionRequest {

    private String endpoint;

    private SubscriptionKey keys;
}

@Getter
@Setter
class SubscriptionKey {
    private String p256dh;

    private String auth;
}
