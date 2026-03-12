package com.zebrunner.automation.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ObjectMapperHolder {

    public static final ObjectMapper COMMON_MAPPER =
            new ObjectMapper().registerModule(new JavaTimeModule())
                              .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                              .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                              .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

}
