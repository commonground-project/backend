package tw.commonground.backend.service.notification.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class NotificationDto {

    private String title;

    private String body;

    private String url;
}
