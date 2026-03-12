package com.zebrunner.automation.api.tcm.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com.zebrunner.automation.api.tcm.domain.request.v1.AddTestRunCaseResultsRequest;

@Data
@NoArgsConstructor
public class TestRunSettings {

    private List<AddTestRunCaseResultsRequest.ResultStatus> resultStatuses;

}
