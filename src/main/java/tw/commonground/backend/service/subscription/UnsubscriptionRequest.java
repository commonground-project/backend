package tw.commonground.backend.service.subscription;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnsubscriptionRequest {
    private String endpoint;

    private UnsubscriptionKey keys;
}

@Getter
@Setter
class UnsubscriptionKey {

    private String p256dh;

    private String auth;
}
