package com.zebrunner.automation.legacy;

import com.zebrunner.automation.api.integration.client.IntegrationClient;
import com.zebrunner.automation.api.integration.domain.Tool;
import com.zebrunner.automation.api.integration.domain.IntegrationResource;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Deprecated
public class IntegrationManager {

    private static final Map<Long, List<IntegrationResource>> INTEGATIONS_MAP = new ConcurrentHashMap<>();


    public static void addIntegration(Long projectId, Tool tool) {
        INTEGATIONS_MAP.putIfAbsent(projectId, new ArrayList<>());
        List<IntegrationResource> integrations = INTEGATIONS_MAP.get(projectId);

        boolean integrationExists = integrations.stream()
                .anyMatch(integration -> integration.getTool().equals(tool));

        if (!integrationExists) {

            IntegrationResource integrationResource = IntegrationClient.create(tool, projectId);
            integrations.add(integrationResource);

            log.info("Integration added for project: " + projectId + " with tool: " + tool.name());
        } else {
            log.info("Integration already exists for project: " + projectId + " with tool: " + tool.name());
        }
    }

    public static void removeAllIntegrationsFromProject(Long projectId) {
        List<IntegrationResource> integrations = INTEGATIONS_MAP.get(projectId);

        if (integrations != null) {

            integrations.forEach(integration -> {

                Tool tool = integration.getTool();
                Long id = integration.getId();

                IntegrationClient.delete(tool, id);

                log.info("Integration with ID " + id + " for tool " + tool.name() + " removed.");
            });

            INTEGATIONS_MAP.remove(projectId);

        } else {
            log.info("No integrations found for project " + projectId);
        }
    }

    public static Map<Long, List<IntegrationResource>> getIntegrationsMap() {
        return INTEGATIONS_MAP;
    }

    public static void removeAllAddedIntegrations() {
        IntegrationManager.getIntegrationsMap()
                .keySet()
                .forEach(IntegrationManager::removeAllIntegrationsFromProject);
    }

}
