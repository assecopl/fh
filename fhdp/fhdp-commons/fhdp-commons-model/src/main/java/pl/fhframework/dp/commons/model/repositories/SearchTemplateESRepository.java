package pl.fhframework.dp.commons.model.repositories;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import pl.fhframework.dp.transport.searchtemplate.SearchTemplateDto;

public interface SearchTemplateESRepository extends ElasticsearchRepository<SearchTemplateDto, Long> {
}
