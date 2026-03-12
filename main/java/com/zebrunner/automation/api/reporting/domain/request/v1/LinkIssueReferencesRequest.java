package com.zebrunner.automation.api.reporting.domain.request.v1;

import com.zebrunner.automation.api.reporting.domain.IssueReference;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class LinkIssueReferencesRequest {


    private List<Item> items;

    @Data
    public static class Item {

        private Long testId;
        private IssueReference.Type type;
        private String value;

        public Item(Long testId, IssueReference.Type type, String issueKey) {
            this.testId = testId;
            this.type = type;
            this.value = issueKey;
        }
    }
}

