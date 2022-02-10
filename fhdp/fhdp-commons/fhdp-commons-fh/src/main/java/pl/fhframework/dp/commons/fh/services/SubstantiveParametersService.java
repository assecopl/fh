package pl.fhframework.dp.commons.fh.services;

import pl.fhframework.dp.commons.fh.utils.rest.facade.GenericFacadeService;
import pl.fhframework.dp.transport.dto.parameters.SubstantiveParametersDto;
import pl.fhframework.dp.transport.dto.parameters.SubstantiveParametersDtoQuery;
import pl.fhframework.dp.transport.service.ISubstantiveParametersService;
import pl.fhframework.core.services.FhService;
import pl.fhframework.model.forms.PageModel;

@FhService
public class SubstantiveParametersService extends GenericFacadeService<Long, SubstantiveParametersDto, SubstantiveParametersDto, SubstantiveParametersDtoQuery> {

    public SubstantiveParametersService() {
        super(ISubstantiveParametersService.class);
    }

    @Override
    public PageModel<SubstantiveParametersDto> listDtoPaged(SubstantiveParametersDtoQuery query) {
        return super.listDtoPaged(query);
    }
}
