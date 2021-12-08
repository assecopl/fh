package pl.fhframework.dp.commons.model.repositories;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import pl.fhframework.dp.transport.dto.parameters.SubstantiveParametersTagDto;

public interface SubstantiveParametersTagESRepository extends ElasticsearchRepository<SubstantiveParametersTagDto, String> {
}
