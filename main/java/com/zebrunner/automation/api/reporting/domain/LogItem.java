package com.zebrunner.automation.api.reporting.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;

import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.TimeZone;

@Slf4j
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LogItem {
    private Long testId;
    private String level;
    private long timestamp;
    private String message;

    public static LogItem generateRandomLogWithLevel(Long testId, String logLevel) {
        return LogItem.builder()
                .testId(testId)
                .level(logLevel)
                .timestamp(OffsetDateTime.now(TimeZone.getTimeZone("America/New_York").toZoneId()).toInstant().toEpochMilli())
                .message("Log message ".concat(RandomStringUtils.randomNumeric(19)))
                .build();
    }

    public String generateLogLineAsOnUi(String timezone) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
        log.info("Current time " + dateFormat.format(timestamp));

        dateFormat.setTimeZone(TimeZone.getTimeZone(timezone));
        String formattedTime = dateFormat.format(timestamp);

        log.info("Formatted  to " + timezone + " timezone time  " + dateFormat.format(timestamp));
        return String.format("%s [%s] %s", formattedTime, this.level, this.message);
    }

}
