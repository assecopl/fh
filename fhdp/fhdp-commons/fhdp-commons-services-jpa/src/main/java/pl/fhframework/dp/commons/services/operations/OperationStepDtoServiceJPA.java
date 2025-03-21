package pl.fhframework.dp.commons.services.operations;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import pl.fhframework.dp.commons.model.dao.OperationStepDAO;
import pl.fhframework.dp.commons.model.entities.OperationStep;
import pl.fhframework.dp.commons.model.repositories.OperationStepJPARepository;
import pl.fhframework.dp.commons.utils.conversion.BeanConversionUtil;
import pl.fhframework.dp.transport.dto.commons.OperationStepDto;
import pl.fhframework.dp.transport.dto.operations.OperationStepDtoQuery;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 29/11/2021
 */
@Service
@Slf4j
public class OperationStepDtoServiceJPA extends OperationStepDtoServiceBase {
    @Autowired
    OperationStepJPARepository operationStepRepository;

    @Autowired
    OperationStepDAO operationStepDAO;


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
        List<OperationStep> list = operationStepDAO.findByQuery(query);
        return list.stream().map(x -> BeanConversionUtil.mapObject(x, true, OperationStepDto.class)).collect(Collectors.toList());
    }

    @Override
    public Long listCount(OperationStepDtoQuery query) {
    	return operationStepDAO.countByQuery(query);
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
