package com.zebrunner.automation.api.tcm.domain;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TestRun {

    private Long id;

    private String title;
    private String description;
    private Long milestoneId;
    private Environment environment;
    private TestPlan testPlan;
    private List<TestRunConfiguration> configurations;
    private List<TestRunStatusStatistic> caseStatusStatistics;

    private List<Requirement> requirements = new ArrayList<>();
    private Boolean closed;

    private Long createdBy;
    private Instant createdAt;
    private Long lastModifiedBy;
    private Instant lastModifiedAt;

    public TestRun(String title) {
        this.title = title;
        this.configurations = new ArrayList<>();
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TestRunStatusStatistic {

        private Long statusId;
        private Long count;

    }

    public static TestRun createWithRandomName() {
        return TestRun.builder()
                .title("Test run № " + RandomStringUtils.randomAlphabetic(5))
                .build();
    }

    @Data
    @NoArgsConstructor
    public static class TestPlan {

        private Long id;
        private String title;

    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Requirement {

        private Object source;
        private String reference;


    }

}
