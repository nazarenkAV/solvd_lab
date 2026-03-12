package com.zebrunner.automation.api.tcm.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class TestCasePriority {

    @EqualsAndHashCode.Exclude
    private Long id;

    private String name;
    private String iconUrl;
    private Boolean isDefault;
    private Integer relativePosition;

    public TestCasePriority(Long id) {
        this.id = id;
    }

    @Getter
    public enum Priority {
        HIGH("High"),
        MIDDLE("Middle"),
        LOW("Low"),
        TRIVIAL("Trivial");

        private final String priority;

        Priority(String priority) {
            this.priority = priority;
        }

    }
}
