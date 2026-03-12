package com.zebrunner.automation.legacy;


import com.zebrunner.automation.api.reporting.domain.Launch;
import com.zebrunner.automation.api.reporting.domain.TestExecution;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@Deprecated
@NoArgsConstructor
@AllArgsConstructor
public class TestClassLaunchDataStorage {

    private Launch launch;

    private List<TestExecution> testsList;

    private List<TestExecution> passedTests;

    private List<TestExecution> failedTests;

}