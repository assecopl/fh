package pl.fhframework.core.cloud.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.fhframework.core.cloud.config.ExposedUseCaseDefinition;

/**
 * Information on cloud use case found in cloud use case registry
 */
@Data
@AllArgsConstructor
public class CloudRegistryUseCaseInfo {

    private String serverName;

    private ExposedUseCaseDefinition useCaseDefinition;
}
