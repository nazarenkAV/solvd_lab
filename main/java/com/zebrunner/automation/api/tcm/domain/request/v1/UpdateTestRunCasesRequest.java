package com.zebrunner.automation.api.tcm.domain.request.v1;

import lombok.Data;

import java.util.List;

@Data
public class UpdateTestRunCasesRequest {

    private List<TestCase> items;

    @Data
    public static class TestCase {

        private Long id;
        private Long assigneeId;

        public TestCase(Long id, Long assigneeId) {
            this.id = id;
            this.assigneeId = assigneeId;
        }

    }

}
