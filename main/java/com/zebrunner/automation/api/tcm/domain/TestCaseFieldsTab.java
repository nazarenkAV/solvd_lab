package com.zebrunner.automation.api.tcm.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestCaseFieldsTab {

    public static final String GENERAL_TAB_NAME = "General";
    public static final String PROPERTIES_TAB_NAME = "Properties";

    private Long id;

    private Long projectId;
    private String name;
    private Integer relativePosition;

    private DisplayMode displayMode;

    public static List<TestCaseFieldsTab> defaultOf(Long projectId) {
        return List.of(
                new TestCaseFieldsTab(null, projectId, GENERAL_TAB_NAME, 1, DisplayMode.ROW),
                new TestCaseFieldsTab(null, projectId, PROPERTIES_TAB_NAME, 2, DisplayMode.COLUMNS)
        );
    }

    public enum DisplayMode {

        ROW,
        COLUMNS

    }

}

