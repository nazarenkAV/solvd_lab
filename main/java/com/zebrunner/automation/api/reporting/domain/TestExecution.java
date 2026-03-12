package com.zebrunner.automation.api.reporting.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.zebrunner.automation.api.reporting.domain.vo.ArtifactReference;

import lombok.*;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TestExecution {

    private Long id;

    @JsonProperty(required = true)
    private String name;

    private String correlationData;

    private String reason;

    private Integer reasonHashCode;

    private String methodName;

    private String className;

    private OffsetDateTime startedAt;

    private OffsetDateTime endedAt;

    @JsonProperty("result")
    private String status;

    private boolean knownIssue;

    private String maintainer;

    private Object testFunction;

    private Set<Object> failureTagAssignments = Set.of();

    private Set<Label> labels = new HashSet<>();

    private Set<String> testGroups = new HashSet<>();

    private IssueReference issueReference;

    private Set<ArtifactReference> artifacts = Set.of();

    private Object testSuiteExecution;

    public static TestExecution getRandomTestExecution() {
        return TestExecution.builder()
                .name("Test № ".concat(RandomStringUtils.randomAlphabetic(5)))
                .startedAt(OffsetDateTime.now())
                .methodName("Method № ".concat(RandomStringUtils.randomAlphabetic(5)))
                .className("Test.class")
                .build();
    }

    public static TestExecution getTestExecution(String testName, String maintainer, String className) {
        return TestExecution.builder()
                .name(testName)
                .startedAt(OffsetDateTime.now())
                .methodName("Method № ".concat(RandomStringUtils.randomAlphabetic(5)))
                .className(className)
                .maintainer(maintainer)
                .build();
    }

    public static TestExecution getTestExecution(String testName, OffsetDateTime startedAt) {
        return TestExecution.builder()
                .name(testName)
                .startedAt(startedAt)
                .methodName("Method № ".concat(RandomStringUtils.randomAlphabetic(5)))
                .className("Test.class")
                .build();
    }

    public enum Status {
        IN_PROGRESS("IN_PROGRESS"),
        PASSED("PASSED"),
        FAILED("FAILED"),
        SKIPPED("SKIPPED"),
        ABORTED("ABORTED");

        private String status;

        Status(String status) {
            this.status = status;
        }

        public String getStatus() {
            return status;
        }
    }
}
