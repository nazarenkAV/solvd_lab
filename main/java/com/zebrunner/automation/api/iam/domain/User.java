package com.zebrunner.automation.api.iam.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

import com.zebrunner.automation.util.JsonCryptoConverter;

@Data
@Builder
@JsonSerialize
@AllArgsConstructor
@Accessors(chain = true)
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    private int id;
    private String username;

    @JsonSerialize(converter = JsonCryptoConverter.class)// to decrypt password
    private String password;
    private Long groupId;

    private String email;
    private String firstName;
    private String lastName;
    private String source;
    private String status;
    private List<Group> groups;
    private List<Permission> permissions;
    private String registrationDateTime;
    private String photoUrl;
    private String lastLogin;
    private List<String> preferences;
    private boolean hasReadOnlyAccess;

    @Deprecated
    @JsonIgnore
    private String zebrunnerRole;

    @JsonCreator
    public User(String username) {
        this.username = username;
    }

}