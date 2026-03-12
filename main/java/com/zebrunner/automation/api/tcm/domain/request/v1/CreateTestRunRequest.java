package com.zebrunner.automation.api.tcm.domain.request.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateTestRunRequest {

    private String title;
    private String description;
    private Long milestoneId;
    private Environment environment;

    private List<Configuration> configurations;
    private List<TestCase> testCases;

    @Data
    @NoArgsConstructor
    public static class Environment {

        private Long id;

    }

    @Data
    @NoArgsConstructor
    public static class Configuration {

        private Long groupId;
        private Long optionId;

        public void setId(Long id) {
            this.optionId = id;
        }

    }

    @Data
    @NoArgsConstructor
    public static class TestCase {

        private Long id;
        private Long assigneeId;

        public TestCase(Long id) {
            this.id = id;
        }

    }

}
