package com.zebrunner.automation.api.tcm.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SharedStepsBunch {

    private Long id;
    private String name;
    private List<TestCaseStep> steps;

    public SharedStepsBunch(Long id) {
        this.id = id;
    }

    public SharedStepsBunch(String name, List<TestCaseStep> steps) {
        this.name = name;
        this.steps = steps;
    }

    public static SharedStepsBunch generateRandom(int numberOfTestCaseSteps) {
        String name = "Shared step name ".concat(RandomStringUtils.randomAlphabetic(5));
        List<TestCaseStep> steps = new ArrayList<>();
        for (int i = 0; i < numberOfTestCaseSteps; i++) {

            String action = "Shared step action №".concat(RandomStringUtils.randomNumeric(9));
            String expectedResult = "Shared step expected result №".concat(RandomStringUtils.randomNumeric(9));

            TestCaseStep step = TestCaseStep.with(action, expectedResult);
            step.setRelativePosition(i + 2);
            steps.add(step);
        }
        return new SharedStepsBunch(name, steps);
    }
}
