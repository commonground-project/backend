package tw.commonground.backend.service.notification.dto;

import lombok.Data;

@Data
public class NotificationDto {

    private String title;

    private String body;

    private String url;
}
