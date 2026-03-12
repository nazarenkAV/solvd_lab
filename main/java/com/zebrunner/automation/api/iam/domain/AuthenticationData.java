package com.zebrunner.automation.api.iam.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthenticationData {

    private Integer userId;
    private Set<String> permissionsSuperset;
    private Set<ProjectAssignment> projectAssignments;

    private String authTokenType;
    private String authToken;
    private int authTokenExpirationInSecs;

    private Instant previousLogin;
    private String refreshToken;

    private String tenantName;
    private String authTokenExpiresAt;

    @Data
    @NoArgsConstructor
    public static class ProjectAssignment {

        private Long projectId;
        private String role;

    }

}
