package tw.commonground.backend.service.push;

import java.io.IOException;
import java.security.GeneralSecurityException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import lombok.Getter;
import lombok.Setter;

import nl.martijndwars.webpush.PushService;
import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.martijndwars.webpush.Notification;


@SpringBootApplication
@RestController
public class WebPushService {

    @Autowired
    private PushService pushService;

    @Getter
    @Setter
    public static class WebPushSubscription {
        private String notificationEndPoint;
        private String publicKey;
        private String auth;
    }

    public static class WebPushMessage {
        public String title;
        public String clickTarget;
        public String message;
    }

    private Map<String, WebPushSubscription> subscriptions = new ConcurrentHashMap<>();

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("/subscribe")
    public void subscribe(WebPushSubscription subscription) {
        subscriptions.put(subscription.notificationEndPoint, subscription);
    }

    @PostMapping("/unsubscribe")
    public void unsubscribe(WebPushSubscription subscription) {
        subscriptions.remove(subscription.notificationEndPoint);
    }

    @PostMapping("/notify-all")
    public WebPushMessage notifyAll(@RequestBody WebPushMessage message) throws GeneralSecurityException, IOException, JoseException, ExecutionException, InterruptedException {
        for (WebPushSubscription subscription: subscriptions.values()) {
            Notification notification = new Notification(
                    subscription.getNotificationEndPoint(),
                    subscription.getPublicKey(),
                    subscription.getAuth(),
                    objectMapper.writeValueAsBytes(message));
            pushService.send(notification);
        }
        return message;
    }
}