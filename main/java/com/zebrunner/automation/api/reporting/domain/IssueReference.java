package com.zebrunner.automation.api.reporting.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssueReference {

    @EqualsAndHashCode.Exclude
    private Long id;
    private Type type;
    private String value;
    public IssueReference(String value, Type type) {
        this.value = value;
        this.type = type;
    }
    public enum Type {
        JIRA,
        GITHUB,
        OTHER
    }
}
