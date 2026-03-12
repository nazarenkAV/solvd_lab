package com.zebrunner.automation.util;

import org.apache.commons.lang3.tuple.Pair;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.zebrunner.automation.api.iam.domain.ApiToken;
import com.zebrunner.automation.api.iam.domain.AuthenticationData;
import com.zebrunner.automation.api.iam.method.v1.CreateApiToken;
import com.zebrunner.automation.api.iam.method.v1.Login;
import com.zebrunner.automation.api.iam.method.v1.RefreshToken;
import com.zebrunner.automation.config.ConfigHelper;
import com.zebrunner.automation.config.UserProperties;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthenticationContext {

    private static final Map<String, String> usernamesToApiTokens = new ConcurrentHashMap<>();
    private static final Map<String, AuthenticationData> usernamesToAuthData = new ConcurrentHashMap<>();

    public static String getOrCreateApiToken(String username, String password) {
        return usernamesToApiTokens.computeIfAbsent(
                username,
                $ -> AuthenticationContext.createApiToken(username, password)
        );
    }

    private static String createApiToken(String username, String password) {
        String name = "Api token for '" + username + "'";
        String authToken = AuthenticationContext.getAuthToken(username, password);

        ApiToken apiToken = CreateApiToken.invoke(name, authToken);

        return apiToken.getValue();
    }

    public static String getTenantAdminAuthToken() {
        UserProperties.Admin admin = ConfigHelper.getUserProperties().getAdmin();

        return AuthenticationContext.getAuthToken(admin.getUsername(), admin.getPassword());
    }

    public static String getAuthToken(String username, String password) {
        AuthenticationData authenticationData = usernamesToAuthData.computeIfAbsent(
                username, $ -> Login.invoke(username, password)
        );

        return authenticationData.getAuthToken();
    }

    public static synchronized void refreshAuthToken(String authToken) {
        Pair<String, AuthenticationData> usernameAndAuthData = AuthenticationContext.getUsernameAndAuthDataByAuthToken(authToken);
        String username = usernameAndAuthData.getLeft();
        AuthenticationData authData = usernameAndAuthData.getRight();

        AuthenticationData refreshedAuthData = RefreshToken.invoke(authData.getRefreshToken());
        usernamesToAuthData.put(username, refreshedAuthData);
    }

    private static Pair<String, AuthenticationData> getUsernameAndAuthDataByAuthToken(String authToken) {
        return StreamUtils.findFirst(
                                  usernamesToAuthData.entrySet(),
                                  usernameAndAuthData -> usernameAndAuthData.getValue().getAuthToken().equals(authToken)
                          )
                          .map(usernameAndAuthData -> Pair.of(usernameAndAuthData.getKey(), usernameAndAuthData.getValue()))
                          .orElseThrow(() -> new RuntimeException("Should never be thrown"));
    }

}
