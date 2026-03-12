package com.zebrunner.automation.api.tcm.service;

import com.zebrunner.automation.api.tcm.domain.Environment;

@Deprecated
public interface EnvironmentService {

    Environment createEnvironment(Long projectId, Environment environment);

    void deleteEnvironment(Long projectId, Long environmentId);
}
