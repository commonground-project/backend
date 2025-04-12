package tw.commonground.backend.service.subscription;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UnsubscriptionRequest {
    private String endpoint;

    private UnsubscriptionKey keys;
}

@Getter
@Setter
@ToString
class UnsubscriptionKey {

    private String p256dh;

    private String auth;
}
