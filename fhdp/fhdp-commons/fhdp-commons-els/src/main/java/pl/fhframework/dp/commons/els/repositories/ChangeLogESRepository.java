package pl.fhframework.dp.commons.els.repositories;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import pl.fhframework.dp.transport.changelog.ChangeLogDto;


/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 */
public interface ChangeLogESRepository extends ElasticsearchRepository<ChangeLogDto, String> {

}
