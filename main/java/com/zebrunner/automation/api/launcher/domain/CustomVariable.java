package com.zebrunner.automation.api.launcher.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomVariable {
    private String name;
    private String defaultValue;
    @Builder.Default
    private Type type = Type.STRING;
    private List<String> values;
    private String value;

    public enum Type {
        STRING,
        BOOLEAN,
        INTEGER,
        CHOICE
    }

    public static CustomVariable getRandomCustomVariable(CustomVariable.Type type) {
        String defaultValue = "Letters, numbers and spaces ".concat(RandomStringUtils.randomNumeric(5));
        String varName = "Letters, numbers and spaces ".concat(RandomStringUtils.randomNumeric(5));
        return CustomVariable.builder()
                .name(varName)
                .type(type)
                .defaultValue(defaultValue)
                .build();
    }

    public static CustomVariable getRandomChoiceVariable() {
        List<String> values = Arrays.asList("value 1", "value 2", "value 3");
        String varName = "Letters, numbers and spaces ".concat(RandomStringUtils.randomNumeric(5));
        return CustomVariable.builder()
                .name(varName)
                .type(Type.CHOICE)
                .defaultValue(values.get(0))
                .values(values)
                .build();
    }
}
