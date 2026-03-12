package com.zebrunner.automation.api.integration.domain;

import com.zebrunner.automation.api.common.DataPayload;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class IntegrationPayload extends DataPayload<IntegrationResource> {
}