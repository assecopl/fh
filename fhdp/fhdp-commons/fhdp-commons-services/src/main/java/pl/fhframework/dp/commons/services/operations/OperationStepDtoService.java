package pl.fhframework.dp.commons.services.operations;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.fhframework.dp.commons.els.repositories.OperationStepESRepository;
import pl.fhframework.dp.commons.services.facade.GenericDtoService;
import pl.fhframework.dp.transport.dto.commons.OperationStepDto;
import pl.fhframework.dp.transport.dto.operations.OperationStepDtoQuery;
import pl.fhframework.dp.transport.service.IOperationStepDtoService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 29/11/2021
 */
@Service
@Slf4j
public class OperationStepDtoService extends GenericDtoService<String, OperationStepDto, OperationStepDto, OperationStepDtoQuery, OperationStepDto> implements IOperationStepDtoService {
    @Autowired
    OperationStepESRepository operationStepESRepository;

    public OperationStepDtoService() {
        super(OperationStepDto.class, OperationStepDto.class, OperationStepDto.class);
    }

    public void logOperationStepStart(String msgKey, String processID, String masterProcessId, String operationGUID, String stepID, Long docId) {
        OperationStepDto dto = findOperationStep(processID, operationGUID, stepID);
        if(dto == null) {
            dto = new OperationStepDto();
            dto.setDescription(msgKey);
            dto.setProcessId(processID);
            dto.setMasterProcessId(masterProcessId);
            dto.setOperationGUID(operationGUID);
            dto.setStepId(stepID);
            dto.setDocID(docId);
            dto.setId(UUID.randomUUID().toString());
            dto.setStarted(LocalDateTime.now());
            persistDto(dto);
        }
    }

    public OperationStepDto findOperationStep(String processID, String operationGUID, String stepID) {
        OperationStepDtoQuery query = new OperationStepDtoQuery();
        query.setProcessId(processID);
        query.setOperationGUID(operationGUID);
        query.setStepId(stepID);
        List<OperationStepDto> list = listDto(query);
        if(list.isEmpty()) {
            return null;
        } else {
            OperationStepDto dto = list.get(0);
            return dto;
        }
    }

    public void logOperationStepFinish(String processID, String operationGUID, String stepID) {
        OperationStepDto dto = findOperationStep(processID, operationGUID, stepID);
        if(dto == null) {
            log.error("Can not find operation step for OpGuid: {}, processId :{}, stepId: {}", operationGUID, processID, stepID);
        } else {
            dto.setFinished(LocalDateTime.now());
            if(dto.getStarted() != null && dto.getFinished() != null) {
                long diff = ChronoUnit.MILLIS.between(dto.getStarted(), dto.getFinished());
                dto.setDuration((float) diff /1000);
            }
            persistDto(dto);
        }
    }

    @Override
    public List<OperationStepDto> listDto(OperationStepDtoQuery query) {
        if(query.getSortProperty() == null) {
            query.setSortProperty("id.keyword");
        }
        return super.listDto(query);
    }

    @Override
    protected BoolQueryBuilder extendQueryBuilder(BoolQueryBuilder builder, OperationStepDtoQuery query) {
        if(query.getOperationGUID() != null) {
            builder.must(QueryBuilders.termsQuery("operationGUID.keyword", query.getOperationGUID()));
        }
        if(query.getDocID() != null) {
            builder.must(QueryBuilders.termQuery("docID", query.getDocID()));
        }
        if(query.getMasterProcessId() != null) {
            builder.must(QueryBuilders.termsQuery("masterProcessId.keyword", query.getMasterProcessId()));
        }
        if(query.getProcessId() != null) {
            builder.must(QueryBuilders.termsQuery("processId.keyword", query.getProcessId()));
        }
        if(query.getStepId() != null) {
            builder.must(QueryBuilders.termsQuery("stepId.keyword", query.getStepId()));
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
