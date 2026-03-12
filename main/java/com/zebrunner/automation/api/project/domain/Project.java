package com.zebrunner.automation.api.project.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Project implements Cloneable, Serializable {

    private Long id;
    private String name;
    private String key;
    private String logoKey;
    private String description;
    private LocalDateTime createdAt;
    private Lead lead;
    private Boolean publiclyAccessible;
    private Boolean deleted;

    public Project(String name, String key) {
        this.name = name;
        this.key = key;
    }

    @Override
    @SneakyThrows
    public Project clone() {
        return SerializationUtils.clone(this);
    }

    @Data
    @NoArgsConstructor
    public static class Lead {

        private Long id;
        private String username;
        private String email;
        private String firstName;
        private String lastName;
        private String photoUrl;
    }

    public String getTrimmedProjectKey() {
        String projectKey = this.key;

        if (projectKey.length() > 4) {
            projectKey = projectKey.substring(0, 4);
        }
        return projectKey;
    }
}

