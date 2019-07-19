package pl.fhframework.core.cloud;

import pl.fhframework.core.cloud.config.CloudRegistryUseCaseInfo;
import pl.fhframework.UserSession;
import pl.fhframework.model.dto.AbstractMessage;

import java.util.Collection;
import java.util.Optional;

/**
 * Interface of a service which creates and maintains cloud information
 */
public interface IExportedCloudServerRegistry {

    public Optional<CloudRegistryUseCaseInfo> findCloudUseCase(String fullClassName);

    public <T extends AbstractMessage> T sendMessage(String serverName, AbstractMessage message, UserSession localUserSession);

    public Collection<String> getCloudUseCases();

    public boolean isConnectionUp(String serverName);
}
