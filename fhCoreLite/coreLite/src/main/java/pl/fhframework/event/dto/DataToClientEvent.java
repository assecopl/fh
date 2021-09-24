package pl.fhframework.event.dto;

import lombok.Getter;
import pl.fhframework.core.model.dto.client.AbstractClientOutputData;

@Getter
public class DataToClientEvent extends EventDTO {
    private AbstractClientOutputData clientData;

    public DataToClientEvent(AbstractClientOutputData clientData) {
        this.clientData = clientData;
    }
}
