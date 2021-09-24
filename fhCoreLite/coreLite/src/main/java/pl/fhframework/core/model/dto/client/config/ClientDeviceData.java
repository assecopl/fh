package pl.fhframework.core.model.dto.client.config;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import lombok.Data;
import pl.fhframework.core.model.dto.client.ClientMessageType;

import java.util.List;

/**
 * Server input message - clients initial message
 */
@Data
@JsonSubTypes(@JsonSubTypes.Type(value = ClientDeviceData.class, name = ClientMessageType.IN_CLIENT_DEVICE_DATA))
public class ClientDeviceData extends AbstractClientConfigData {
    private String serviceDescription;

    private DeviceTypeEnum deviceType;

    private List<DeviceDescriptor> devicesDescriptor;

    public ClientDeviceData() {
        super(ClientMessageType.IN_CLIENT_DEVICE_DATA);
    }
}
