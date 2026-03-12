package com.zebrunner.automation.api.tcm.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@Builder
public class TestCase {

    private Long id;
    private String key;
    private Long testSuiteId;
    private Integer relativePosition;
    private Boolean deleted;
    @EqualsAndHashCode.Exclude
    private Instant deletedAt;
    @EqualsAndHashCode.Exclude
    private Instant deletedBy;

    @EqualsAndHashCode.Exclude
    private Instant createdAt;
    private Long createdBy;
    @EqualsAndHashCode.Exclude
    private Instant lastModifiedAt;
    private Long lastModifiedBy;

    private String title;
    private String description;
    private TestCasePriority priority;
    private TestCaseAutomationState automationState;
    private Boolean draft;
    private Boolean deprecated;
    private List<Attachment> attachments;
    private List<CustomField> customFields;

    private String preConditions;
    private String postConditions;
    private List<Step> steps;

    public TestCase(String title) {
        this.title = title;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Step {

        @EqualsAndHashCode.Exclude
        private Long id;
        private TestCaseStepType type;
        private Integer relativePosition;
        private SharedStepsBunch sharedSteps;
        private TestCaseStep regularStep;

        private Step(TestCaseStep regularStep) {
            this.type = TestCaseStepType.REGULAR;
            this.regularStep = regularStep;
            this.relativePosition = 1;
        }

        private Step(SharedStepsBunch sharedSteps) {
            this.type = TestCaseStepType.SHARED;
            this.sharedSteps = sharedSteps;
            this.relativePosition = 2;
        }

        public static Step shared(SharedStepsBunch sharedSteps) {
            return new Step(sharedSteps);
        }

        public static Step regular(TestCaseStep regularStep) {
            return new Step(regularStep);
        }

        public Step withRelativePosition(Integer relativePosition) {
            this.relativePosition = relativePosition;
            return this;
        }

    }

    public void forEachStep(Consumer<Step> action) {
        if (this.steps == null) {
            this.steps = new ArrayList<>();
        }

        this.steps.forEach(action);
    }

    public void addStep(Step step) {
        if (this.steps == null) {
            this.steps = new ArrayList<>();
        }

        this.steps.add(step);
    }

}
