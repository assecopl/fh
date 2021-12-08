package pl.fhframework.dp.commons.services.alert;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.fhframework.dp.commons.camunda.repositories.AlertESRepository;
import pl.fhframework.dp.commons.services.auditlog.AuditLogDtoService;
import pl.fhframework.dp.commons.services.facade.GenericDtoService;
import pl.fhframework.dp.commons.utils.conversion.BeanConversionUtil;
import pl.fhframework.dp.transport.dto.alerts.AlertCodeEnum;
import pl.fhframework.dp.transport.dto.alerts.AlertDto;
import pl.fhframework.dp.transport.dto.alerts.AlertDtoQuery;
import pl.fhframework.dp.transport.service.IAlertDtoService;

import java.util.List;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 23/03/2021
 */
@Service
public class AlertDtoService extends GenericDtoService<String, AlertDto, AlertDto, AlertDtoQuery, AlertDto> implements IAlertDtoService {

    @Autowired
    AlertESRepository alertESRepository;
    @Autowired
    AuditLogDtoService auditLogDtoService;

    public AlertDtoService() {
        super(AlertDto.class, AlertDto.class, AlertDto.class);
    }

    @Override
    public List<AlertDto> listDto(AlertDtoQuery query) {
        if(StringUtils.isEmpty(query.getSortProperty())) {
            query.setSortProperty("id.keyword");
        }
        return super.listDto(query);
    }

    @Override
    public AlertDto getDto(String key) {
        return alertESRepository.findById(key).orElse(null);
    }

    @Override
    public String persistDto(AlertDto alertDto) {
        return alertESRepository.save(alertDto).getId();
    }

    @Override
    public void deleteDto(String key) {
        AlertDto entity = getDto(key);
        if(entity != null) {
            alertESRepository.delete(entity);
        }
    }

    public AlertDto createAlert(AlertCodeEnum code, String office, long declarationId, String kindOfDeclaration, String lrn, String mrn) {
        AlertDto ret = getAlert(code, office, declarationId);
        if(ret == null) ret = new AlertDto();
        ret.setName(code.getCodeName());
        ret.setCode(code);
        ret.setOffice(office);
        ret.setDeclarationId(declarationId);
        ret.setDescription(code.getDescription());
        ret.setGuidelines(code.getGuidelines());
        ret.setRoles(code.getRoles());
        ret.setKindOfDeclaration(kindOfDeclaration);
        ret.setLrn(lrn);
        ret.setMrn(mrn);
        String msgKey = "$.audit.alert.create\b" + code.name() + "\b" + ret.getId();
        auditLogDtoService.logBusinessInfo("alerts", msgKey, BeanConversionUtil.toJson(ret), null, null, ret.getId(), "system");
        persistDto(ret);
        return ret;
    }

    private AlertDto getAlert(AlertCodeEnum code, String office, long declarationId) {
        AlertDtoQuery query = new AlertDtoQuery();
        query.setCode(code);
        query.setOffice(office);
        query.setDeclarationId(declarationId);
        List<AlertDto> list = listDto(query);
        if(list.isEmpty()) return null;
        else return list.get(0);
    }

    public void removeAlert(String id) {
        String msgKey = "$.audit.alert.remove\b" + id;
        auditLogDtoService.logBusinessInfo("alerts", msgKey, id , null, null, id, "system");
        deleteDto(id);
    }

    @Override
    protected BoolQueryBuilder extendQueryBuilder(BoolQueryBuilder builder, AlertDtoQuery query) {
        if(query.getCode() != null) {
            builder.must(QueryBuilders.termQuery("code.keyword", query.getCode().name()));
        }
        if(query.getDeclarationId() != null) {
            builder.must(QueryBuilders.termQuery("declarationId", query.getDeclarationId()));
        }
        if(query.getOffice() != null) {
            builder.must(QueryBuilders.termQuery("office.keyword", query.getOffice()));
        }
        if(query.getRoles() != null && !query.getRoles().isEmpty()) {
            builder.must(QueryBuilders.termsQuery("roles.keyword", query.getRoles()));
        }
        return builder;
    }
}
