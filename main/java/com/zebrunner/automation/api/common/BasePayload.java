package com.zebrunner.automation.api.common;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Getter
@NoArgsConstructor
public abstract class BasePayload<T> {

    @JsonAlias("_meta")
    private final Map<String, Object> meta = new HashMap<>();
    @JsonAlias("_links")
    private final Map<String, String> links = new HashMap<>();

    public Map<String, Object> getMeta() {
        return Collections.unmodifiableMap(meta);
    }

    public Object getMetaProperty(String name) {
        return meta.get(name);
    }

}
