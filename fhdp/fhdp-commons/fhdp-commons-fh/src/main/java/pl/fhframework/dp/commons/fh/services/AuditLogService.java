package pl.fhframework.dp.commons.fh.services;

import pl.fhframework.core.services.FhService;
import pl.fhframework.dp.commons.fh.utils.rest.facade.GenericFacadeService;
import pl.fhframework.dp.transport.auditlog.AuditLogDto;
import pl.fhframework.dp.transport.auditlog.AuditLogDtoQuery;
import pl.fhframework.dp.transport.service.IAuditLogDtoService;
import pl.fhframework.model.forms.PageModel;

@FhService
public class AuditLogService extends GenericFacadeService<String, AuditLogDto, AuditLogDto, AuditLogDtoQuery> {

    public AuditLogService() {
        super(IAuditLogDtoService.class);
    }

    @Override
    public PageModel<AuditLogDto> listDtoPaged(AuditLogDtoQuery query) {
        return super.listDtoPaged(query);
    }
}
