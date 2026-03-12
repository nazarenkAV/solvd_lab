package com.zebrunner.automation.util;

import com.fasterxml.jackson.databind.util.StdConverter;
import com.zebrunner.carina.utils.encryptor.EncryptorUtils;

public class JsonCryptoConverter extends StdConverter<String, String> {

    @Override
    public String convert(String secretText) {
        return EncryptorUtils.decrypt(secretText);
    }
}
