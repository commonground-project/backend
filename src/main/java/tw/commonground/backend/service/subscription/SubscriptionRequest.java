package tw.commonground.backend.service.subscription;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SubscriptionRequest {

    private String endpoint;

    private SubscriptionKey keys;
}

@Getter
@Setter
@ToString
class SubscriptionKey {

    private String p256dh;

    private String auth;
}
