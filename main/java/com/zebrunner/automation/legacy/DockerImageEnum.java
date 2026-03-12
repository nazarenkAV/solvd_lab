package com.zebrunner.automation.legacy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DockerImageEnum {
    MAVEN_3_8_11("maven:3.8-openjdk-11", "mvn clean test --no-transfer-progress"),
    MAVEN_3_8_8("maven:3.8-openjdk-8", "mvn clean test --no-transfer-progress"),
    GRADLE_7_2_11("gradle:7.2-jdk11", "gradle clean test"),
    GRADLE_7_2_8("gradle:7.2-jdk8", "gradle clean test"),
    PYTHON_LATEST("python:latest", "pip install -r requirements.txt && pytest"),
    MICROSOFT_PLAYWRIGHT("mcr.microsoft.com/playwright:latest", "yarn && npx playwright test"),
    AMANCEVICE_PANDAS_1_1_4("amancevice/pandas:1.1.4",
            "pip install -r requirements.txt && pabot --processes 1 --testlevelsplit --pabotlib --listener robotframework_zebrunner.ZebrunnerListener ./ ; python -m robotframework_zebrunner attach-reports"),
    CYPRESS_LATEST("public.ecr.aws/zebrunner/cyserver:latest", "npm install && npx cypress run --headed");

    private final String dockerImage;
    private final String launchCommand;

}
