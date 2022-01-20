package pl.fhframework.dp.commons.els.repositories;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import pl.fhframework.dp.transport.dto.parameters.SubstantiveParametersDto;

public interface SubstantiveParametersESRepository extends ElasticsearchRepository<SubstantiveParametersDto, String> {
    SubstantiveParametersDto findByKey(String key);
}
