package tw.commonground.backend.service.notification;

import tw.commonground.backend.service.notification.dto.NotificationDto;

public final class NotificationFactory {

    private static final int MAX_CONTENT_LENGTH = 10;

    private static final String VIEWPOINT_REPLY_NOTIFICATION_TITLE_TEMPLATE = "%s 下有一則新的回覆！";

    private static final String VIEWPOINT_REPLY_NOTIFICATION_BODY_TEMPLATE = "「%s」";

    private static final String VIEWPOINT_REPLY_NOTIFICATION_URL_TEMPLATE = "/issues/%s/viewpoints/%s";

    private static final String QUOTE_REPLY_NOTIFICATION_TITLE_TEMPLATE = "有人節錄了您的回覆！";

    private static final String QUOTE_REPLY_NOTIFICATION_BODY_TEMPLATE = "「%s」";

    private static final String QUOTE_REPLY_NOTIFICATION_URL_TEMPLATE = "/issues/%s/viewpoints/%s";

    private NotificationFactory() {
        // hide the constructor
    }

    public static NotificationDto createViewpointReplyNotification(String viewpointTitle, String replyContent,
                                                                   String issueId, String viewpointId) {

        String content = replyContent.length() > MAX_CONTENT_LENGTH
                ? replyContent.substring(0, MAX_CONTENT_LENGTH) + "..." : replyContent;

        NotificationDto dto = new NotificationDto();
        dto.setTitle(String.format(VIEWPOINT_REPLY_NOTIFICATION_TITLE_TEMPLATE, viewpointTitle));
        dto.setBody(String.format(VIEWPOINT_REPLY_NOTIFICATION_BODY_TEMPLATE, content));
        dto.setUrl(String.format(VIEWPOINT_REPLY_NOTIFICATION_URL_TEMPLATE, issueId, viewpointId));
        return dto;
    }

    public static NotificationDto createQuoteReplyNotification(String replyContent,
                                                               String issueId, String viewpointId) {

        String content = replyContent.length() > MAX_CONTENT_LENGTH
                ? replyContent.substring(0, MAX_CONTENT_LENGTH) + "..." : replyContent;

        NotificationDto dto = new NotificationDto();
        dto.setTitle(QUOTE_REPLY_NOTIFICATION_TITLE_TEMPLATE);
        dto.setBody(String.format(QUOTE_REPLY_NOTIFICATION_BODY_TEMPLATE, content));
        dto.setUrl(String.format(QUOTE_REPLY_NOTIFICATION_URL_TEMPLATE, issueId, viewpointId));
        return dto;
    }
}
