package pl.fhframework.events;

/**
 * Created by pawel.ruta on 2018-11-05.
 */

import pl.fhframework.core.model.dto.client.AbstractClientMessage;

import java.util.Objects;

public interface IClientDataHandler<T extends AbstractClientMessage> {
    void handleClientData(T clientData);

    String getSupportedType();

    String getServiceId();

    default boolean isHandling(T clientData) {
        return Objects.equals(clientData.getServiceId(), getServiceId()) &&
                Objects.equals(clientData.getType(), getSupportedType());
    }
}
