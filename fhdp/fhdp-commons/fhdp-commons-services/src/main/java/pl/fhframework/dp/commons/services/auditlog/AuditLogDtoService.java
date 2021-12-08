package pl.fhframework.dp.commons.services.auditlog;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.fhframework.dp.commons.camunda.repositories.AuditLogESRepository;
import pl.fhframework.dp.commons.services.facade.GenericDtoService;
import pl.fhframework.dp.transport.auditlog.AuditLogDto;
import pl.fhframework.dp.transport.auditlog.AuditLogDtoQuery;
import pl.fhframework.dp.transport.auditlog.AuditLogTypeEnum;
import pl.fhframework.dp.transport.dto.declaration.SeverityEnum;
import pl.fhframework.dp.transport.service.IAuditLogDtoService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 15/09/2020
 */
@Service
@Slf4j
public class AuditLogDtoService extends GenericDtoService<String, AuditLogDto, AuditLogDto, AuditLogDtoQuery, AuditLogDto> implements IAuditLogDtoService {

    @Autowired
    AuditLogESRepository auditLogESRepository;

    public AuditLogDtoService() {
        super(AuditLogDto.class, AuditLogDto.class, AuditLogDto.class);
    }

    @Override
    public List<AuditLogDto> listDto(AuditLogDtoQuery query) {
        if(query.getSortProperty() == null || "id".equals(query.getSortProperty())) {
            query.setSortProperty("eventTime");
        }
        return super.listDto(query);
    }

    @Override
    public String persistDto(AuditLogDto auditLogDto) {
        return auditLogESRepository.save(auditLogDto).getId();
    }

    @Override
    protected BoolQueryBuilder extendQueryBuilder(BoolQueryBuilder builder, AuditLogDtoQuery query) {
        if(query.getType() != null) {
            builder.must(QueryBuilders.termsQuery("type.keyword", query.getType().name()));
        }
        if(query.getSeverity() != null) {
            builder.must(QueryBuilders.termsQuery("severity.keyword", query.getSeverity().name()));
        }
        if(query.getCategory() != null) {
            builder.must(QueryBuilders.termsQuery("category.keyword", query.getCategory()));
        }
        if(query.getMessageKey() != null) {
            builder.must(QueryBuilders.wildcardQuery("messageKey", query.getMessageKey() + "*"));
        }
        if(query.getComment() != null) {
            builder.must(QueryBuilders.wildcardQuery("comment", query.getComment() + "*"));
        }
        if(query.getProcessID() != null) {
            builder.must(QueryBuilders.termsQuery("processID.keyword", query.getProcessID()));
        }
        if(query.getStepID() != null) {
            builder.must(QueryBuilders.termsQuery("stepID.keyword", query.getStepID()));
        }
        if(query.getOperationGUID() != null) {
            builder.must(QueryBuilders.termsQuery("operationGUID.keyword", query.getOperationGUID()));
        }
        if(query.getUserLogin() != null) {
            builder.must(QueryBuilders.wildcardQuery("userLogin", query.getUserLogin() + "*"));
        }
        LocalDateTime dateFrom = query.getEventTimeFrom();
        LocalDateTime dateTo = query.getEventTimeTo();
        if (dateFrom != null || dateTo != null) {
            builder.filter(QueryBuilders.rangeQuery("eventTime")
                    .from(dateFrom)
                    .to(dateTo)
                    .includeLower(dateFrom != null)
                    .includeUpper(dateTo != null));
        }
        return builder;
    }

    /**
     *
     * @param severity
     * @param category
     * @param messageKey - format: $.key\bparam1\bparam2
     * @param comment
     * @param processID
     * @param operationGUID
     * @param stepID
     * @param userLogin
     */
    public void logBusiness(SeverityEnum severity,
                            String category,
                            String messageKey,
                            String comment,
                            String processID,
                            String operationGUID,
                            String stepID,
                            String userLogin){
        AuditLogDto dto = new AuditLogDto(AuditLogTypeEnum.business,
                severity,
                category,
                LocalDateTime.now(),
                messageKey,
                comment,
                processID,
                operationGUID,
                stepID,
                userLogin);
        persistDto(dto);

    }

    public void logOperationStepStart(String messageKey,
                          String processID,
                          String operationGUID,
                          String stepID) {
        AuditLogDto dto = new AuditLogDto(AuditLogTypeEnum.business,
                SeverityEnum.info,
                "operationSteps",
                LocalDateTime.now(),
                messageKey,
                "start",
                processID,
                operationGUID,
                stepID,
                "system");
        persistDto(dto);
    }

    public void logOperationStepFinish(String processID,
                                      String operationGUID,
                                      String stepID) {
        AuditLogDtoQuery query = new AuditLogDtoQuery();
        query.setProcessID(processID);
        query.setOperationGUID(operationGUID);
        query.setStepID(stepID);
        query.setCategory("operationSteps");
        query.setAscending(false);
        List<AuditLogDto> auditSteps = listDto(query);
        if(!auditSteps.isEmpty()) {
            AuditLogDto dto = auditSteps.get(0);
            dto.setEndTime(LocalDateTime.now());
            persistDto(dto);
        }
    }

    public void logBusinessInfo(
                            String category,
                            String messageKey,
                            String comment,
                            String processID,
                            String operationGUID,
                            String stepID,
                            String userLogin){
        AuditLogDto dto = new AuditLogDto(AuditLogTypeEnum.business,
                SeverityEnum.info,
                category,
                LocalDateTime.now(),
                messageKey,
                comment,
                processID,
                operationGUID,
                stepID,
                userLogin);
        persistDto(dto);

    }

    public void logBusinessInfo(
            String category,
            String messageKey,
            String comment,
            Long declarationId,
            String userLogin){
        AuditLogDto dto = new AuditLogDto(AuditLogTypeEnum.business,
                SeverityEnum.info,
                category,
                LocalDateTime.now(),
                messageKey,
                comment,
                null,
                null,
                null,
                userLogin);
        dto.setDeclarationId(declarationId);
        persistDto(dto);

    }

    public void logBusinessError(
            String category,
            String messageKey,
            String comment,
            String processID,
            String operationGUID,
            String stepID,
            String userLogin){
        AuditLogDto dto = new AuditLogDto(AuditLogTypeEnum.business,
                SeverityEnum.error,
                category,
                LocalDateTime.now(),
                messageKey,
                comment,
                processID,
                operationGUID,
                stepID,
                userLogin);
        persistDto(dto);
    }

    public void logBusinessError(
            String category,
            String messageKey,
            String comment,
            Long declarationId,
            String userLogin){
        AuditLogDto dto = new AuditLogDto(AuditLogTypeEnum.business,
                SeverityEnum.error,
                category,
                LocalDateTime.now(),
                messageKey,
                comment,
                null,
                null,
                null,
                userLogin);
        dto.setDeclarationId(declarationId);
        persistDto(dto);

    }

    public void logTechnicalInfo(
            String category,
            String messageKey,
            String comment,
            String processID,
            String operationGUID,
            String stepID,
            String userLogin){
        AuditLogDto dto = new AuditLogDto(AuditLogTypeEnum.technical,
                SeverityEnum.info,
                category,
                LocalDateTime.now(),
                messageKey,
                comment,
                processID,
                operationGUID,
                stepID,
                userLogin);
        persistDto(dto);
    }

    public void logTechnicalInfo(
            String category,
            String messageKey,
            String comment,
            Long declarationId,
            String userLogin){
        AuditLogDto dto = new AuditLogDto(AuditLogTypeEnum.technical,
                SeverityEnum.info,
                category,
                LocalDateTime.now(),
                messageKey,
                comment,
                null,
                null,
                null,
                userLogin);
        dto.setDeclarationId(declarationId);
        persistDto(dto);
    }

    public void logTechnicalError(
            String category,
            String messageKey,
            String comment,
            String processID,
            String operationGUID,
            String stepID,
            String userLogin){
        AuditLogDto dto = new AuditLogDto(AuditLogTypeEnum.technical,
                SeverityEnum.error,
                category,
                LocalDateTime.now(),
                messageKey,
                comment,
                processID,
                operationGUID,
                stepID,
                userLogin);
        persistDto(dto);
    }

    public void logTechnicalError(
            String category,
            String messageKey,
            String comment,
            Long declarationId,
            String userLogin){
        AuditLogDto dto = new AuditLogDto(AuditLogTypeEnum.technical,
                SeverityEnum.error,
                category,
                LocalDateTime.now(),
                messageKey,
                comment,
                null,
                null,
                null,
                userLogin);
        dto.setDeclarationId(declarationId);
        persistDto(dto);
    }
}
