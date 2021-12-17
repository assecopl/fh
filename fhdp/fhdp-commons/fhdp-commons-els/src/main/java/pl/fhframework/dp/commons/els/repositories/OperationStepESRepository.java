package pl.fhframework.dp.commons.els.repositories;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import pl.fhframework.dp.transport.dto.operations.OperationStepDto;


/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version $Revision:  $, $Date:  $
 * @created 2021-11-29
 */
public interface OperationStepESRepository extends ElasticsearchRepository<OperationStepDto, String> {

}
