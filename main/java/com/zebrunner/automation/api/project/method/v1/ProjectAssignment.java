package com.zebrunner.automation.api.project.method.v1;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zebrunner.automation.legacy.RoleEnum;
import com.zebrunner.automation.api.iam.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectAssignment {
    private Long id;
    private User user;
    private RoleEnum role;

    private Instant createdAt;
    private Long createdBy;
    private Instant modifiedAt;
    private Long modifiedBy;
    @JsonAlias("_meta")
    private final Map<String, Object> meta = new HashMap<>();
}