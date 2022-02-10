package pl.fhframework.dp.commons.services.endpointcfg;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.dp.commons.base.model.IEndpointCfgDefinition;
import pl.fhframework.dp.commons.els.repositories.EndpointCfgESRepository;
import pl.fhframework.dp.transport.endpoints.EndpointCfgDto;
import pl.fhframework.dp.transport.endpoints.IEndpointCfgService;

import java.util.Optional;


public abstract class EndpointCfgServiceBase implements IEndpointCfgService {

	private final EndpointCfgESRepository endpointCfgESRepository;

	@Autowired
	public EndpointCfgServiceBase(EndpointCfgESRepository endpointCfgESRepository) {
		this.endpointCfgESRepository = endpointCfgESRepository;
	}

	/**
	 * Returns the configuration dto based on the Elasticsearch's entry,
	 * if there is no item a new one is being created
	 * @param endpointCfgDefinition
	 * @return
	 */
	@Override
	public EndpointCfgDto getEndpointCfg(IEndpointCfgDefinition endpointCfgDefinition) {
		Optional<EndpointCfgDto> element = getElement(endpointCfgDefinition);
		return (element.isPresent()) ? element.get() : handleEndpointCfg(endpointCfgDefinition);
	}

	/**
	 * Gets the web service's configuration and based on it, creates a new entry in the Elasticsearch.
	 * @param endpointCfgDefinition
	 * @return
	 */
	protected abstract EndpointCfgDto handleEndpointCfg(IEndpointCfgDefinition endpointCfgDefinition);

	/**
	 * Based on the system and service names, returns the element from the Elasticsearch
	 * @param endpointCfgDefinition
	 * @return
	 */
	protected Optional<EndpointCfgDto> getElement(IEndpointCfgDefinition endpointCfgDefinition) {
		return endpointCfgESRepository.getBySystemNameAndServiceName(
				endpointCfgDefinition.getSystemName(),
				endpointCfgDefinition.getServiceName()
		);
	}

	/**
	 * Stores the sent dto into the Elasticsearch
	 * @param def
	 * @return
	 */
	@Override
	public EndpointCfgDto storeEndpointCfg(EndpointCfgDto def) {
		return endpointCfgESRepository.save(def);
	}
}
