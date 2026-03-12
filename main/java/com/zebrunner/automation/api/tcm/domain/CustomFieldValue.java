package com.zebrunner.automation.api.tcm.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomFieldValue {

    public static final int MULTI_SELECT_MAX_OPTIONS = 24;

    @JsonProperty("default")
    private String defaultValue;

    private Boolean hasIcon;
    private List<Option> options;

    private String timeZone;
    private Boolean today;
    private Integer relativeToToday;
    private LocalDate specificDate;

    private Boolean author;
    private String specificUser;


    private boolean isSingleDefaultDropdownOptionProvided() {
        if (options != null) {
            long numberOfDefault = options.stream()
                    .filter(option -> Boolean.TRUE.equals(option.getIsDefault()))
                    .count();
            return numberOfDefault <= 1;
        }
        return true;
    }

    private boolean isTimeZoneProvided() {
        if (today != null || relativeToToday != null) {
            return timeZone != null;
        }
        return true;
    }

    private boolean isNotMoreThanOneMutuallyExclusiveDateFieldProvided() {
        int numberOfFields = today != null ? 1 : 0;
        numberOfFields += relativeToToday != null ? 1 : 0;
        numberOfFields += specificDate != null ? 1 : 0;

        return numberOfFields <= 1;
    }

    private boolean isNotMoreThanOneMutuallyExclusiveUserFieldProvided() {
        int numberOfFields = author != null ? 1 : 0;
        numberOfFields += specificUser != null ? 1 : 0;

        return numberOfFields <= 1;
    }


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Option {

        private Integer id;

        private String name;

        private Object icon;

        private Integer relativePosition;
        private Boolean isDefault;
        private String value;

        public void generateMissingIdAndValue(Function<Option, String> valueResolver) {
            if (this.id == null) {
                this.id = RandomUtils.nextInt();
            }
            this.value = valueResolver.apply(this);
        }

    }

}
