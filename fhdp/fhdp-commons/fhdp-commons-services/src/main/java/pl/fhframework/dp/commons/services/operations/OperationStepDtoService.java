package pl.fhframework.dp.commons.services.operations;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import pl.fhframework.dp.commons.els.repositories.OperationStepESRepository;
import pl.fhframework.dp.commons.services.facade.GenericDtoService;
import pl.fhframework.dp.transport.dto.operations.OperationStepDto;
import pl.fhframework.dp.transport.dto.operations.OperationStepDtoQuery;
import pl.fhframework.dp.transport.service.IOperationStepDtoService;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 29/11/2021
 */
//@Component
public class OperationStepDtoService extends GenericDtoService<String, OperationStepDto, OperationStepDto, OperationStepDtoQuery, OperationStepDto> implements IOperationStepDtoService {
//    @Autowired
    OperationStepESRepository operationStepESRepository;

    public OperationStepDtoService(Class<OperationStepDto> operationStepDtoClass, Class<OperationStepDto> dtoClazz, Class<OperationStepDto> entityClazz) {
        super(operationStepDtoClass, dtoClazz, entityClazz);
    }

    @Override
    protected BoolQueryBuilder extendQueryBuilder(BoolQueryBuilder builder, OperationStepDtoQuery query) {
        if(query.getOperationGUID() != null) {
            builder.must(QueryBuilders.termQuery("operationGUID.keyword", query.getOperationGUID()));
        }
        if(query.getDocID() != null) {
            builder.must(QueryBuilders.termQuery("docID", query.getDocID()));
        }
        if(query.getMasterProcessId() != null) {
            builder.must(QueryBuilders.termQuery("masterProcessId.keyword", query.getMasterProcessId()));
        }
        if(query.getProcessId() != null) {
            builder.must(QueryBuilders.termQuery("processId.keyword", query.getProcessId()));
        }
        if(query.getStepId() != null) {
            builder.must(QueryBuilders.termQuery("stepId.keyword", query.getStepId()));
        }
        return builder;
    }

    @Override
    public OperationStepDto getDto(String key) {
        return operationStepESRepository.findById(key).orElse(null);
    }

    @Override
    public String persistDto(OperationStepDto operationStepDto) {
        return operationStepESRepository.save(operationStepDto).getId();
    }
}
