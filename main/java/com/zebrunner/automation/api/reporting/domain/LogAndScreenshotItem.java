package com.zebrunner.automation.api.reporting.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class LogAndScreenshotItem {
    private String kind;
    private String level;
    private Instant instant;
    private String value;
}
