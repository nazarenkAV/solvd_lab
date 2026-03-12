package com.zebrunner.automation.legacy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Deprecated
@Getter
@RequiredArgsConstructor
public enum TcmType {

    ZEBRUNNER("com.zebrunner.app/tcm.zebrunner.test-case-key"),
    TESTRAIL("com.zebrunner.app/tcm.testrail.case-id"),
    ZEPHYR("com.zebrunner.app/tcm.zephyr.test-case-key"),
    XRAY("com.zebrunner.app/tcm.xray.test-key");

    private final String labelKey;

}
