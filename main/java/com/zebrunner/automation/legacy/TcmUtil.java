package com.zebrunner.automation.legacy;

import com.zebrunner.agent.core.registrar.TestCase;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Deprecated
public class TcmUtil {

    public static void setTestRunId(String tcmRunId) {
        if (!(tcmRunId == null)) {
            log.info("Results will be reported to run with id {}", tcmRunId);
            TestCase.setTestRunId(tcmRunId);
        }
    }
}
