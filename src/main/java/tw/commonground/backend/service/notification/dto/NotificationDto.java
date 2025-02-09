package tw.commonground.backend.service.notification.dto;

import lombok.Data;

import java.net.URL;

@Data
public class NotificationDto {

    private String title;

    private String body;

    private URL url;
}
