package com.zebrunner.automation.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateUtil {

    public static Date parseDate(String dateStr, SimpleDateFormat dateFormat) {
        try {
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Failed to parse date: " + dateStr, e);
        }
    }

    public static ZonedDateTime parseDate(String dateStr, ZoneId currentZoneId, ZoneId formateZoneId, DateTimeFormatter formatter) {
        LocalDateTime localDateTime = LocalDateTime.parse(dateStr, formatter);
        ZonedDateTime zonedDateTimeLocal = localDateTime.atZone(currentZoneId);
        return zonedDateTimeLocal.withZoneSameInstant(formateZoneId);
    }

    public static boolean isTimeWithinTolerance(
            LocalTime currentTime, String expectedTime, int toleranceMinutes, DateTimeFormatter timeFormatter
    ) {
        LocalTime expectedLocalTime = LocalTime.parse(expectedTime, timeFormatter);

        Duration duration = Duration.between(expectedLocalTime, currentTime);
        long timeDifference = Math.abs(duration.toMinutes());

        return timeDifference <= toleranceMinutes;
    }

    public static boolean isDateWithinTolerance(
            String actualDate, LocalDateTime expectedDate, int toleranceMinutes, DateTimeFormatter dateTimeFormatter
    ) {
        LocalDateTime expectedLocalDateTime = LocalDateTime.parse(actualDate, dateTimeFormatter);

        Duration duration = Duration.between(expectedLocalDateTime, expectedDate);
        long timeDifference = Math.abs(duration.toMinutes());

        return timeDifference <= toleranceMinutes;
    }

    public static Instant convertToInstant(String timeTriggeredStr, ZoneId zone) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");
        LocalDateTime localDateTime = LocalDateTime.parse(timeTriggeredStr, formatter);

        ZonedDateTime zonedDateTime = localDateTime.atZone(zone);
        return zonedDateTime.toInstant();
    }

    public static String formatTime(Instant currentTime, ZoneId zone, DateTimeFormatter formatter) {
        return currentTime.atZone(zone).format(formatter);
    }

    public static LocalDateTime getByPatternAndFormat(String s, String pattern, String format) {
        Pattern regexPattern = Pattern.compile(pattern);
        Matcher matcher = regexPattern.matcher(s);

        if (matcher.find()) {
            String timeString = matcher.group(1).trim();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            return LocalDateTime.parse(timeString, formatter);
        } else {
            throw new RuntimeException("Pattern not found in the input string !");
        }
    }
}
