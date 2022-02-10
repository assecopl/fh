package pl.fhframework.dp.commons.els.repositories;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import pl.fhframework.dp.transport.auditlog.AuditLogDto;


/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version $Revision:  $, $Date:  $
 * @created 2019-02-15
 */
public interface AuditLogESRepository extends ElasticsearchRepository<AuditLogDto, String> {

}
