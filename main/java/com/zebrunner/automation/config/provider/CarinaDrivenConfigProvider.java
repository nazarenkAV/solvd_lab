package com.zebrunner.automation.config.provider;

import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

import com.zebrunner.carina.utils.R;

public class CarinaDrivenConfigProvider implements ConfigProvider {

    @Override
    public String getStringByKey(String key) {
        return Optional.ofNullable(R.CONFIG.getDecrypted(key))
                       .filter(StringUtils::isNotBlank)

                       .or(() -> Optional.ofNullable(R.TESTDATA.getDecrypted(key)))

                       .orElse(null);
    }

}
