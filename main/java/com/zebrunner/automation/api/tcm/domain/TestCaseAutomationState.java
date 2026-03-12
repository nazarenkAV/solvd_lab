package com.zebrunner.automation.api.tcm.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TestCaseAutomationState {

    @EqualsAndHashCode.Exclude
    private Long id;

    private String name;
    private String iconUrl;
    private Boolean isDefault;
    private Integer relativePosition;

    public TestCaseAutomationState(Long id) {
        this.id = id;
    }

}
