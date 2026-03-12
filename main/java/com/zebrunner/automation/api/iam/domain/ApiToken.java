package com.zebrunner.automation.api.iam.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.restassured.common.mapper.TypeRef;

import lombok.Data;

import com.zebrunner.automation.api.common.DataPayload;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiToken {

    public static final TypeRef<DataPayload<ApiToken>> DATA_PAYLOAD_TYPE = new TypeRef<>() {};

    private String value;

}
