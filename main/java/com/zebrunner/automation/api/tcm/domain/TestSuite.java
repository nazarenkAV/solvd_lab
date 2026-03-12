package com.zebrunner.automation.api.tcm.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TestSuite {

    private Long id;
    private Long parentSuiteId;
    private Integer relativePosition;
    private String title;
    private String description;
    private String preConditions;

    public TestSuite(String title) {
        this.title = title;
    }

    public TestSuite(String title, Long parentSuiteId) {
        this.title = title;
        this.parentSuiteId = parentSuiteId;
    }

    public static TestSuite withRandomName() {
        return new TestSuite("Suite " + UUID.randomUUID());
    }

    public static TestSuite withRandomName(Long parentSuiteId) {
        return new TestSuite("Sub-suite for parent(with ID=" + parentSuiteId + ")" + UUID.randomUUID(), parentSuiteId);
    }
}
