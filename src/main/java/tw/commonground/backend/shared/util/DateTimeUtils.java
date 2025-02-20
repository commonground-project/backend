package tw.commonground.backend.shared.util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public final class DateTimeUtils {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .withZone(ZoneOffset.UTC);

    private DateTimeUtils() {
        // hide constructor
    }

    public static String toIso8601String(LocalDateTime dateTime) {
        return (dateTime != null) ? FORMATTER.format(dateTime.atZone(ZoneOffset.UTC).toInstant()) : null;
    }
}
