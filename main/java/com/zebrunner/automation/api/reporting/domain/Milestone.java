package com.zebrunner.automation.api.reporting.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Milestone {
    private Long id;
    private String name;
    private boolean completed;
    private String description;
    private Instant startDate;
    private Instant dueDate;

    public static Milestone createMilestoneWithTitleAndDueDate(String milestoneTitle, Instant dueDate) {
        return Milestone.builder()
                .name(milestoneTitle)
                .dueDate(dueDate)
                .build();
    }

    public static Milestone createMilestoneWithTitle(String milestoneTitle) {
        return Milestone.builder()
                .name(milestoneTitle)
                .build();
    }
}
