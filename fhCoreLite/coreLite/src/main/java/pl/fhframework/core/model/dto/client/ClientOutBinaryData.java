package pl.fhframework.core.model.dto.client;

import lombok.Data;

/**
 * Server input message - clients initial message
 */
@Data
public class ClientOutBinaryData extends AbstractClientOutputData {
    private byte[] data;

    public ClientOutBinaryData(String serviceId, byte[] data) {
        super(serviceId);
        this.data = data;
    }
}
