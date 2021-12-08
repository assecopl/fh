package pl.fhframework.dp.commons.fh.services;

import pl.fhframework.dp.commons.fh.utils.rest.facade.GenericFacadeService;
import pl.fhframework.dp.transport.dto.alerts.AlertDto;
import pl.fhframework.dp.transport.dto.alerts.AlertDtoQuery;
import pl.fhframework.dp.transport.service.IAlertDtoService;

import java.util.List;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 07/12/2021
 */
public class DeclarationAlertService extends GenericFacadeService<String, AlertDto, AlertDto, AlertDtoQuery> {
    public DeclarationAlertService() {
        super(IAlertDtoService.class);
    }


    @Override
    public List<AlertDto> listDto(AlertDtoQuery query) {
        return super.listDto(query);
    }
}
