package tw.commonground.backend.service.subscription.exception;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationDeliveryException extends Exception {

    private final Map<String, Exception> exceptions;

    public NotificationDeliveryException(List<Exception> exceptions, List<String> endpoints) {
        this.exceptions = new HashMap<>();
        for (int index = 0; index < exceptions.size(); index++) {
            this.exceptions.put(endpoints.get(index), exceptions.get(index));
        }
    }

    public NotificationDeliveryException(String title, Exception exception) {
        this.exceptions = new HashMap<>();
        this.exceptions.put(title, exception);
    }

    @Override
    public String toString() {
        return exceptions.toString();
    }
}
