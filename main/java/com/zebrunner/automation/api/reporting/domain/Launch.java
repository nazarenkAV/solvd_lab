package com.zebrunner.automation.api.reporting.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Launch {

    private Long id;

    private String uuid;

    private String name;

    private OffsetDateTime startedAt;

    private OffsetDateTime endedAt;

    private String status;

    private String framework;

    private Milestone milestone;

    private Config config;

    public static Launch getRandomLaunch() {
        return Launch.builder()
               .name("Launch № ".concat(RandomStringUtils.randomAlphabetic(5)))
               .startedAt(OffsetDateTime.now())
               .framework("Framework")
               .build();
    }

    public static Launch getLaunchWithConfigAndMilestone(String env, String build, String milestoneName) {
        return Launch.builder()
                .name("Launch № ".concat(RandomStringUtils.randomAlphabetic(5)))
                .startedAt(OffsetDateTime.now())
                .framework("Framework")
                .config(new Config(env, build))
                .milestone(new Milestone(milestoneName))
                .build();
    }

    @Data
    @NoArgsConstructor
    public static class Milestone {
        private Long id;
        private String name;

        public Milestone(String name) {
            this.name = name;
        }
    }

    @Data
    @NoArgsConstructor
    public static class Config {
        private String environment;
        private String build;
        private boolean treatSkipsAsFailures = true;

        public Config(String environment, String build) {
            this.environment = environment;
            this.build = build;
        }
    }

}
