package com.zebrunner.automation.api.iam.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Group {

    private Integer id;
    private String name;
    private Boolean isDefault = false;
    private Boolean invitable = true;
}


