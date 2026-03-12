package com.zebrunner.automation.config;

import org.apache.commons.lang3.StringUtils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

import com.zebrunner.automation.config.provider.CarinaDrivenConfigProvider;
import com.zebrunner.automation.config.provider.ConfigProvider;
import com.zebrunner.automation.config.provider.GitRepositoryConfigProvider;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigHelper {

    private static final List<ConfigProvider> configProviders = List.of(
            new GitRepositoryConfigProvider(),
            new CarinaDrivenConfigProvider()
    );

    private static UserProperties userProperties;
    private static EmailAccountProperties emailAccountProperties;

    private static JiraProperties jiraProperties;
    private static TestRailProperties testRailProperties;

    private static GitlabProperties gitlabProperties;
    private static GithubProperties githubProperties;
    private static BitbucketProperties bitbucketProperties;

    private static SauceLabsProperties sauceLabsProperties;
    private static LambdaTestProperties lambdaTestProperties;
    private static BrowserStackProperties browserStackProperties;

    private static SlackProperties slackProperties;

    public static String getLandingUrl() {
        return "https://zebrunner.com";
    }

    public static String getTenantUrl() {
        String env = ConfigHelper.getStringByKey("env");

        return ConfigHelper.getStringByKey(env + ".base");
    }

    public synchronized static UserProperties getUserProperties() {
        if (userProperties == null) {
            String prefix = "test.user";

            userProperties = new UserProperties(
                    new UserProperties.Admin(
                            ConfigHelper.getStringByKeyOrNull(prefix + ".admin.username"),
                            ConfigHelper.getStringByKeyOrNull(prefix + ".admin.password")
                    ),
                    new UserProperties.TestUser(
                            ConfigHelper.getStringByKeyOrNull(prefix + ".test-user.username"),
                            ConfigHelper.getStringByKeyOrNull(prefix + ".test-user.password")
                    )
            );
        }

        return userProperties;
    }

    // Email provider
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public synchronized static EmailAccountProperties getEmailAccountProperties() {
        if (emailAccountProperties == null) {
            String prefix = "test.email-account";

            emailAccountProperties = new EmailAccountProperties(
                    ConfigHelper.getBooleanByKeyOrTrue(prefix + ".enabled"),
                    ConfigHelper.getStringByKeyOrNull(prefix + ".username"),
                    ConfigHelper.getStringByKeyOrNull(prefix + ".password")
            );
        }

        return emailAccountProperties;
    }

    // Issue-tracking systems
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public synchronized static JiraProperties getJiraProperties() {
        if (jiraProperties == null) {
            String prefix = "test.external-service.jira";

            jiraProperties = new JiraProperties(
                    ConfigHelper.getBooleanByKeyOrTrue(prefix + ".enabled"),
                    ConfigHelper.getStringByKeyOrNull(prefix + ".url"),
                    ConfigHelper.getStringByKeyOrNull(prefix + ".username"),
                    ConfigHelper.getStringByKeyOrNull(prefix + ".token")
            );
        }

        return jiraProperties;
    }

    public synchronized static TestRailProperties getTestRailProperties() {
        if (testRailProperties == null) {
            String prefix = "test.external-service.test-rail";

            testRailProperties = new TestRailProperties(
                    ConfigHelper.getBooleanByKeyOrTrue(prefix + ".enabled"),
                    ConfigHelper.getStringByKeyOrNull(prefix + ".url"),
                    ConfigHelper.getStringByKeyOrNull(prefix + ".username"),
                    ConfigHelper.getStringByKeyOrNull(prefix + ".password")
            );
        }

        return testRailProperties;
    }

    // Version control systems
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public synchronized static GitlabProperties getGitlabProperties() {
        if (gitlabProperties == null) {
            String prefix = "test.external-service.gitlab";

            gitlabProperties = new GitlabProperties(
                    ConfigHelper.getBooleanByKeyOrTrue(prefix + ".enabled"),
                    ConfigHelper.getStringByKeyOrNull(prefix + ".url"),
                    ConfigHelper.getStringByKeyOrNull(prefix + ".username"),
                    ConfigHelper.getStringByKeyOrNull(prefix + ".access-token")
            );
        }

        return gitlabProperties;
    }

    public synchronized static GithubProperties getGithubProperties() {
        if (githubProperties == null) {
            String prefix = "test.external-service.github";

            githubProperties = new GithubProperties(
                    ConfigHelper.getBooleanByKeyOrTrue(prefix + ".enabled"),
                    ConfigHelper.getStringByKeyOrNull(prefix + ".url"),
                    ConfigHelper.getStringByKeyOrNull(prefix + ".username"),
                    ConfigHelper.getStringByKeyOrNull(prefix + ".access-token"),

                    new GithubProperties.PublicRepository(
                            ConfigHelper.getStringByKey(prefix + ".public-repository.url")
                    )
            );
        }

        return githubProperties;
    }

    public synchronized static BitbucketProperties getBitbucketProperties() {
        if (bitbucketProperties == null) {
            String prefix = "test.external-service.bitbucket";

            bitbucketProperties = new BitbucketProperties(
                    ConfigHelper.getBooleanByKeyOrTrue(prefix + ".enabled"),
                    ConfigHelper.getStringByKeyOrNull(prefix + ".url"),
                    ConfigHelper.getStringByKeyOrNull(prefix + ".username"),
                    ConfigHelper.getStringByKeyOrNull(prefix + ".access-token")
            );
        }

        return bitbucketProperties;
    }

    // Testing platforms
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public synchronized static SauceLabsProperties getSauceLabsProperties() {
        if (sauceLabsProperties == null) {
            String prefix = "test.external-service.sauce-labs";

            sauceLabsProperties = new SauceLabsProperties(
                    ConfigHelper.getBooleanByKeyOrTrue(prefix + ".enabled"),
                    ConfigHelper.getStringByKeyOrNull(prefix + ".hub-url"),
                    ConfigHelper.getStringByKeyOrNull(prefix + ".username"),
                    ConfigHelper.getStringByKeyOrNull(prefix + ".access-key")
            );
        }

        return sauceLabsProperties;
    }

    public synchronized static LambdaTestProperties getLambdaTestProperties() {
        if (lambdaTestProperties == null) {
            String prefix = "test.external-service.lambda-test";

            lambdaTestProperties = new LambdaTestProperties(
                    ConfigHelper.getBooleanByKeyOrTrue(prefix + ".enabled"),
                    ConfigHelper.getStringByKeyOrNull(prefix + ".hub-url"),
                    ConfigHelper.getStringByKeyOrNull(prefix + ".username"),
                    ConfigHelper.getStringByKeyOrNull(prefix + ".access-key")
            );
        }

        return lambdaTestProperties;
    }

    public synchronized static BrowserStackProperties getBrowserStackProperties() {
        if (browserStackProperties == null) {
            String prefix = "test.external-service.browser-stack";

            browserStackProperties = new BrowserStackProperties(
                    ConfigHelper.getBooleanByKeyOrTrue(prefix + ".enabled"),
                    ConfigHelper.getStringByKeyOrNull(prefix + ".hub-url"),
                    ConfigHelper.getStringByKeyOrNull(prefix + ".username"),
                    ConfigHelper.getStringByKeyOrNull(prefix + ".access-key")
            );
        }

        return browserStackProperties;
    }

    // Notification systems
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public synchronized static SlackProperties getSlackProperties() {
        if (slackProperties == null) {
            String prefix = "test.external-service.slack";

            slackProperties = new SlackProperties(
                    ConfigHelper.getBooleanByKeyOrTrue(prefix + ".enabled"),
                    ConfigHelper.getStringByKeyOrNull(prefix + ".bot-name"),
                    ConfigHelper.getStringByKeyOrNull(prefix + ".token")
            );
        }

        return slackProperties;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static String getStringByKey(String... keys) {
        String property = ConfigHelper.getStringByKeyOrNull(keys);

        if (StringUtils.isBlank(property)) {
            throw new RuntimeException("Config property with any key '" + Arrays.toString(keys) + "' must not be blank for tests configuration");
        }

        return property;
    }

    private static Boolean getBooleanByKeyOrTrue(String... keys) {
        String property = ConfigHelper.getStringByKeyOrNull(keys);

        return StringUtils.isBlank(property) || "true".equalsIgnoreCase(property);
    }

    private static String getStringByKeyOrNull(String... keys) {
        for (String key : keys) {
            for (ConfigProvider configProvider : configProviders) {
                String property = configProvider.getStringByKey(key);

                if (StringUtils.isNotBlank(property)) {
                    return property;
                }
            }
        }

        return null;
    }

}
