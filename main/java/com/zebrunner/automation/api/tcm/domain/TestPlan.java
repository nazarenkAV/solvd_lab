package com.zebrunner.automation.api.tcm.domain;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TestPlan {
    private Long id;
    private String title;
    private String description;
    private Long milestoneId;
    private Environment environment;
    private Boolean closed;
    private Long createdBy;
    private Instant createdAt;
    private List<TestRunConfiguration> configurations;

    public static TestPlan generateTestPlanWithRandomName() {
        return TestPlan.builder()
                .title("Test plan " + RandomStringUtils.randomAlphabetic(5))
                .build();
    }

    public TestPlan(String title) {
        this.title = title;
    }
}
