package pl.fhframework.dp.commons.camunda.repositories;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import pl.fhframework.dp.transport.msg.MessageDto;


/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version $Revision:  $, $Date:  $
 * @created 2019-02-15
 */
public interface MessageESRepository extends ElasticsearchRepository<MessageDto, String> {

    MessageDto findByRepositoryId(String repositoryId);

}
