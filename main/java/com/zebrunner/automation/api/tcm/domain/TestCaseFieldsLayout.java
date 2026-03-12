package com.zebrunner.automation.api.tcm.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestCaseFieldsLayout {

    private List<TestCaseFieldsTab> tabs;
    private List<GeneralField> fields;

}
