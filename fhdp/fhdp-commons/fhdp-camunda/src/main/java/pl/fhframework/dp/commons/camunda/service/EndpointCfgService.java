package pl.fhframework.dp.commons.camunda.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.fhframework.dp.commons.base.model.IEndpointCfgDefinition;
import pl.fhframework.dp.commons.camunda.repositories.EndpointCfgESRepository;
import pl.fhframework.dp.transport.endpoints.EndpointCfgDto;
import pl.fhframework.dp.transport.endpoints.IEndpointCfgService;

import java.util.Optional;


@Service
public class EndpointCfgService implements IEndpointCfgService {

	private final EndpointCfgESRepository endpointCfgESRepository;

	@Autowired
	public EndpointCfgService(EndpointCfgESRepository endpointCfgESRepository) {
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
	 * Gets the web service's configuration and based on it, creates a new entry in the Elasticsearch
	 * @param endpointCfgDefinition
	 * @return
	 */
	private EndpointCfgDto handleEndpointCfg(IEndpointCfgDefinition endpointCfgDefinition) {
		EndpointCfg endpointCfg = EndpointCfg.valueOf(endpointCfgDefinition.getSystemName().concat("_").concat(endpointCfgDefinition.getServiceName()));
		addEndpointToElasticsearch(endpointCfg);
		Optional<EndpointCfgDto> element = getElement(endpointCfgDefinition);
		return element.get();
	}

	/**
	 * Creates a new item in the Elasticsearch
	 * @param endpointCfg
	 */
	private void addEndpointToElasticsearch(EndpointCfg endpointCfg) {
		EndpointCfgDto def = new EndpointCfgDto();
		BeanUtils.copyProperties(endpointCfg, def);
		storeEndpointCfg(def);
	}

	/**
	 * Based on the system and service names, returns the element from the Elasticsearch
	 * @param endpointCfgDefinition
	 * @return
	 */
	private Optional<EndpointCfgDto> getElement(IEndpointCfgDefinition endpointCfgDefinition) {
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
