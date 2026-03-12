package com.zebrunner.automation.config.provider;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.yaml.snakeyaml.Yaml;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
public class GitRepositoryConfigProvider implements ConfigProvider {

    private static final String CONFIG_FILE_NAME = "tests-ui.yaml";

    private static final String REPOSITORY_URL_ENV_VAR_NAME = "TESTS_CONFIGURATION_GIT_REPOSITORY_URL";
    private static final String BRANCH_NAME_ENV_VAR_NAME = "TESTS_CONFIGURATION_GIT_REPOSITORY_BRANCH";
    private static final String REPOSITORY_USERNAME_ENV_VAR_NAME = "TESTS_CONFIGURATION_GIT_REPOSITORY_USERNAME";
    private static final String REPOSITORY_ACCESS_TOKEN_ENV_VAR_NAME = "TESTS_CONFIGURATION_GIT_REPOSITORY_ACCESS_TOKEN";

    private Map<String, Object> configuration;

    @Nullable
    @Override
    @SneakyThrows
    public String getStringByKey(String key) {
        String repositoryUrl = this.getRepositoryUrl();
        if (StringUtils.isBlank(repositoryUrl)) {
            log.debug("'{}' environment variable is blank. Returning 'null' as default value", REPOSITORY_URL_ENV_VAR_NAME);
            return null;
        }

        if (configuration == null) {
            this.downloadConfiguration();
        }

        String[] keyParts = key.split("\\.");
        Map<String, Object> yamlMapping = configuration;

        for (int i = 0; i < keyParts.length; i++) {
            Object keyPartValue = yamlMapping.get(keyParts[i]);
            if (keyPartValue == null) {
                break;
            }

            if (keyPartValue instanceof String) {
                if (i != keyParts.length - 1) {
                    break;
                }
                return (String) keyPartValue;

            } else if (keyPartValue instanceof Map) {
                if (i == keyParts.length - 1) {
                    break;
                }
                //noinspection unchecked
                yamlMapping = (Map<String, Object>) keyPartValue;

            } else if (keyPartValue instanceof Boolean) {
                return keyPartValue.toString();
            } else {
                throw new RuntimeException("Unexpected value for key '" + key + "' and key part '" + keyParts[i] + "'");
            }
        }

        log.warn("Could not get property by key '{}': check it presents in '{}' file. Returning 'null' as default value.", key, CONFIG_FILE_NAME);
        return null;
    }

    @SneakyThrows
    private synchronized void downloadConfiguration() {
        if (configuration != null) {
            return;
        }

        String repositoryUrl = this.getRepositoryUrl();
        String branchName = this.getBranchName();

        String username = this.getUsername();
        String accessToken = this.getAccessToken();
        UsernamePasswordCredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(username, accessToken);

        try (
                Git git = Git.cloneRepository()
                             .setURI(repositoryUrl)
                             .setBranch(branchName)
                             .setCredentialsProvider(credentialsProvider)
                             .call();

                Repository repository = git.getRepository();
        ) {
            ObjectId lastCommitId = repository.resolve(Constants.HEAD);

            String configFileContent = this.getConfigFileContent(repository, lastCommitId);

            Yaml yaml = new Yaml();
            this.configuration = yaml.load(configFileContent);
        }
    }

    @SneakyThrows
    private String getConfigFileContent(Repository repository, ObjectId commitId) {
        try (RevWalk revWalk = new RevWalk(repository)) {
            RevCommit commit = revWalk.parseCommit(commitId);

            try (TreeWalk treeWalk = new TreeWalk(repository)) {
                treeWalk.addTree(commit.getTree());
                treeWalk.setRecursive(true);
                treeWalk.setFilter(PathFilter.create(CONFIG_FILE_NAME));

                if (!treeWalk.next()) {
                    throw new RuntimeException("'" + CONFIG_FILE_NAME + "' not found");
                }

                ObjectId objectId = treeWalk.getObjectId(0);
                ObjectLoader loader = repository.open(objectId);

                return new String(loader.getBytes(), StandardCharsets.UTF_8);
            }
        }
    }

    @Nullable
    private String getRepositoryUrl() {
        return StringUtils.trim(System.getenv(REPOSITORY_URL_ENV_VAR_NAME));
    }

    private String getBranchName() {
        String branchName = System.getenv(BRANCH_NAME_ENV_VAR_NAME);

        if (StringUtils.isBlank(branchName)) {
            throw new RuntimeException("Could not resolve tests configuration: '" + BRANCH_NAME_ENV_VAR_NAME + "' must not be blank");
        }

        return branchName.trim();
    }

    private String getUsername() {
        String username = System.getenv(REPOSITORY_USERNAME_ENV_VAR_NAME);

        if (username == null) {
            throw new RuntimeException("Could not resolve tests configuration: '" + REPOSITORY_USERNAME_ENV_VAR_NAME + "' must not be null");
        }

        return username.trim();
    }

    private String getAccessToken() {
        String accessToken = System.getenv(REPOSITORY_ACCESS_TOKEN_ENV_VAR_NAME);

        if (accessToken == null) {
            throw new RuntimeException("Could not resolve tests configuration: '" + REPOSITORY_ACCESS_TOKEN_ENV_VAR_NAME + "' must not be null");
        }

        return accessToken.trim();
    }

}
