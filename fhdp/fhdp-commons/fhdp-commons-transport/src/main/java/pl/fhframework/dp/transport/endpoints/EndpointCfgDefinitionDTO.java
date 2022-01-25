package pl.fhframework.dp.transport.endpoints;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.dp.commons.base.model.IEndpointCfgDefinition;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 08/09/2020
 */
@Getter @Setter
public class EndpointCfgDefinitionDTO implements IEndpointCfgDefinition {
    private String systemName;
    private String serviceName;

    public EndpointCfgDefinitionDTO(String systemName, String serviceName) {
        this.systemName = systemName;
        this.serviceName = serviceName;
    }
}
