package pl.fhframework.core.model.dto.client;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Container for any message being sent to client.
 */
@Getter
@Setter
public class ClientOutCommand extends AbstractClientOutputData {
    private Command command;

    private Map<String, Object> parameters;

    public ClientOutCommand(String serviceId, Command command) {
        this(serviceId, command, null);
    }

    public ClientOutCommand(String serviceId, Command command, Map<String, Object> parameters) {
        super(serviceId);
        this.command = command;
        this.parameters = parameters;
    }

    public static enum Command {
        INIT,
        START,
        STOP,
        REFRESH,
        ;
    }
}
