package com.zebrunner.automation.config.provider;

import javax.annotation.Nullable;

public interface ConfigProvider {

    @Nullable
    String getStringByKey(String key);

}
