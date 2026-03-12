package com.zebrunner.automation.api.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorResponse {

    private String requestId;
    private String code;
    private String message;
    private List<Error> errors;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Error {

        private String source;
        private String message;

    }

}
