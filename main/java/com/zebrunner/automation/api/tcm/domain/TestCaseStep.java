package com.zebrunner.automation.api.tcm.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TestCaseStep {

    private Long id;
    private String action;
    private String expectedResult;
    private Integer relativePosition;

    public TestCaseStep(String action, String expectedResult) {
        this.action = action;
        this.expectedResult = expectedResult;
        this.relativePosition = 1;
    }

    public static TestCaseStep with(String action, String expectedResult) {
        return new TestCaseStep(action, expectedResult);
    }

    public static TestCaseStep withRandomActionAndExpectedResult() {
        String action = "Action №".concat(RandomStringUtils.randomNumeric(9));
        String expectedResult = "Expected result №".concat(RandomStringUtils.randomNumeric(9));
        return new TestCaseStep(action, expectedResult);
    }

    public static List<TestCaseStep> generateRandomSteps(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> TestCaseStep.withRandomActionAndExpectedResult())
                .collect(Collectors.toList());
    }
}
