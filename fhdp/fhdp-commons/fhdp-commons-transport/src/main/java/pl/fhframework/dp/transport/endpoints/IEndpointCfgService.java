package pl.fhframework.dp.transport.endpoints;

import pl.fhframework.dp.commons.base.model.IEndpointCfgDefinition;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 10/02/2021
 */
public interface IEndpointCfgService {
    EndpointCfgDto getEndpointCfg(IEndpointCfgDefinition endpointCfgDefinition);

    EndpointCfgDto storeEndpointCfg(EndpointCfgDto def);
}
