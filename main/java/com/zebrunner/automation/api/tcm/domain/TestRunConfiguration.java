package com.zebrunner.automation.api.tcm.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TestRunConfiguration {

    private Long id;

    private Long projectId;
    private Long groupId;
    private String groupName;
    private Long optionId;
    private String optionName;

}
