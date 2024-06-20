package pl.fhframework.dp.commons.services.operations;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import pl.fhframework.dp.commons.els.repositories.OperationStepESRepository;
import pl.fhframework.dp.commons.mongo.entities.OperationStep;
import pl.fhframework.dp.commons.mongo.repositories.OperationStepRepository;
import pl.fhframework.dp.commons.services.facade.GenericDtoService;
import pl.fhframework.dp.commons.utils.conversion.BeanConversionUtil;
import pl.fhframework.dp.transport.dto.commons.OperationStepDto;
import pl.fhframework.dp.transport.dto.operations.OperationStepDtoQuery;
import pl.fhframework.dp.transport.service.IOperationStepDtoService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 29/11/2021
 */
@Service
@Slf4j
public class OperationStepDtoService implements IOperationStepDtoService {
    @Autowired
    OperationStepRepository operationStepRepository;
    @Autowired
    private MongoTemplate mongoTemplate;


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
        long time = System.nanoTime();
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
        BigDecimal duration = new BigDecimal((System.nanoTime() - time) / (1000.0 * 1000 * 1000)).setScale(3, RoundingMode.HALF_UP);
        if(duration.compareTo(BigDecimal.valueOf(3L)) > 0) {
            log.warn("***** logOperationStepFinish for step {} in operation {} in process {} took {}s !", stepID, operationGUID, processID,  duration);
        }
    }

    @Override
    public List<OperationStepDto> listDto(OperationStepDtoQuery query) {
        Query mongoQuery = createQuery(query);
        List<OperationStep> list = mongoTemplate.find(mongoQuery, OperationStep.class);
        return list.stream().map(x -> BeanConversionUtil.mapObject(x, true, OperationStepDto.class)).collect(Collectors.toList());
    }

    @Override
    public Long listCount(OperationStepDtoQuery query) {
        Query mongoQuery = createQuery(query);
        return mongoTemplate.count(mongoQuery, OperationStep.class);
    }

    private Query createQuery(OperationStepDtoQuery query) {
        Query mongoQuery = new Query();
        List<Criteria> criteria = new ArrayList<>();

        if (query.getOperationGUID() != null) {
            criteria.add(Criteria.where("operationGUID").is(query.getOperationGUID()));
        }

        if (query.getDocID() != null) {
            criteria.add(Criteria.where("docID").is(query.getDocID()));
        }

        if(query.getMasterProcessId() != null) {
            criteria.add(Criteria.where("masterProcessId").is(query.getMasterProcessId()));
        }

        if(query.getProcessId() != null) {
            criteria.add(Criteria.where("processId").is(query.getProcessId()));
        }

        if(query.getStepId() != null) {
            criteria.add(Criteria.where("stepId").is(query.getStepId()));
        }

        if (!criteria.isEmpty()) {
            mongoQuery.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[0])));
        }
        return mongoQuery;
    }

    @Override
    public OperationStepDto getDto(String key) {
        OperationStep operationStep = operationStepRepository.findById(key).orElse(null);
        if(operationStep == null) {
            return null;
        } else {
            return BeanConversionUtil.mapObject(operationStep, false, OperationStepDto.class);
        }
    }

    @Override
    public String persistDto(OperationStepDto operationStepDto) {
        OperationStep operationStep = BeanConversionUtil.mapObject(operationStepDto, false, OperationStep.class);
        return operationStepRepository.save(operationStep).getId();
    }
}
