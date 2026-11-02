package traceit.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateTimeUtils {
    public static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static String formatDisplay(LocalDateTime dateTime) {
        if (dateTime == null) return "N/A";
        return dateTime.format(DISPLAY_FORMATTER);
    }

    public static String formatIso(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(ISO_FORMATTER);
    }

    public static LocalDateTime parse(String text) {
        if (text == null || text.trim().isEmpty()) return null;
        text = text.trim();

        try {
            return LocalDateTime.parse(text, ISO_FORMATTER);
        } catch (DateTimeParseException ignored) {}

        try {
            return LocalDateTime.parse(text, DISPLAY_FORMATTER);
        } catch (DateTimeParseException ignored) {}

        if (text.length() == 10) {
            try {
                return LocalDateTime.parse(text + "T00:00:00");
            } catch (DateTimeParseException ignored) {}
        }

        return null;
    }
}
