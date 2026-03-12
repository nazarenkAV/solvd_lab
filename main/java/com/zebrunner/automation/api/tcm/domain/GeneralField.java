package com.zebrunner.automation.api.tcm.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeneralField {

    private Long id;
    private Long projectId;
    private String description;

    private Long tabId;
    private Integer relativePosition;
    private String name;
    private Boolean enabled;
    private DataType dataType;
    private String type;
    private String value;
    private String valueDefinition;

    public enum DataType {

        DESCRIPTION,
        PRE_CONDITIONS,
        POST_CONDITIONS,
        STEPS,

        AUTHOR,
        CREATED_ON,
        PRIORITY,
        AUTOMATION_STATE,
        DEPRECATED,
        DRAFT,

        ATTACHMENTS,
        REQUIREMENTS,

        //for custom field
        DATE,
        DROPDOWN,
        MULTI_SELECT,
        STRING,
        TEXT,
        URI,
        USER;

    }
}
