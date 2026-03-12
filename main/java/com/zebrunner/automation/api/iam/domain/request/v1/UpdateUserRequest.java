package com.zebrunner.automation.api.iam.domain.request.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateUserRequest {

    private String firstName;
    private String lastName;
    private String photoUrl;

    private String email;
    private Status status;
    private Boolean hasReadOnlyAccess;

    public static UpdateUserRequest with(Status status) {
        return UpdateUserRequest.builder()
                .status(status)
                .build();
    }

    public static UpdateUserRequest with(String email) {
        return UpdateUserRequest.builder()
                .email(email)
                .build();
    }

    public enum Status {

        ACTIVE,
        INACTIVE

    }


}

