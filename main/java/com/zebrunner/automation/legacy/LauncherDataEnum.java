package com.zebrunner.automation.legacy;

@Deprecated
public enum LauncherDataEnum {
    // ----- platforms -----
    LAMBDATEST("Lambdatest"),
    M_CLOUD("Zebrunner Device Farm"),
    SAUCE_LABS("Sauce Labs"),
    BROWSER_STACK("BrowserStack"),
    ZEBRUNNER_SELENIUM_GRID("Zebrunner Engine"),
    TESTING_BOT("Testingbot"),
    // ----- browsers -----
    EDGE("Edge"),
    CHROME("Chrome"),
    FIREFOX("Firefox"),
    SAFARI("Safari"),
    IE("IE"),
    OPERA("Opera"),
    // ----- OS type -----
    WEB("Web"),
    NATIVE("Native"),
    // ----- OS -----
    LINUX("Linux"),
    ANDROID("Android"),
    IOS("iOS"),
    // ----- Device -----
    REDROID("Redroid"),
    // ----- docker image -----
    MAVEN_3_8_11("maven:3.8-openjdk-11"),
    MAVEN_3_8_8("maven:3.8-openjdk-8"),
    GRADLE_7_2_11("gradle:7.2-jdk11"),
    GRADLE_7_2_8("gradle:7.2-jdk8"),
    PYTHON_LATEST("python:latest"),
    CYPRESS_LATEST("public.ecr.aws/zebrunner/cypress:latest"),
    // ----- launch command -----
    MVN_CLEAN_TEST_D_SUITE("mvn clean test -Dsuite="),
    MVN_CLEAN_TEST("mvn clean test"),
    // ----- branches -----
    MASTER("master"),
    MAIN("main"),
    REACT("react");

    private final String data;

    LauncherDataEnum(String data) {
        this.data = data;
    }

    public String get() {
        return data;
    }

}
