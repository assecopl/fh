package pl.fhframework.dp.commons.els.repositories;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import pl.fhframework.dp.transport.dto.commons.OperationStepDto;


/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version $Revision:  $, $Date:  $
 * @created 2021-11-29
 */
public interface OperationStepESRepository extends ElasticsearchRepository<OperationStepDto, String> {

}
