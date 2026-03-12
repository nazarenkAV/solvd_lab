package com.zebrunner.automation.api.iam.service;

import com.zebrunner.automation.api.iam.domain.AuthenticationData;
import com.zebrunner.automation.api.iam.domain.User;

@Deprecated
public interface IAMService {
    AuthenticationData login(User user);

}
