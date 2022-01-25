package pl.fhframework.dp.commons.fh.services;

import pl.fhframework.dp.commons.fh.utils.rest.facade.GenericFacadeService;
import pl.fhframework.dp.transport.dto.parameters.SubstantiveParametersTagDto;
import pl.fhframework.dp.transport.dto.parameters.SubstantiveParametersTagDtoQuery;
import pl.fhframework.dp.transport.service.ISubstantiveParametersTagDtoService;
import pl.fhframework.core.services.FhService;
import pl.fhframework.model.forms.PageModel;

import java.util.List;

@FhService
public class SubstantiveParametersTagService extends GenericFacadeService<String, SubstantiveParametersTagDto, SubstantiveParametersTagDto, SubstantiveParametersTagDtoQuery> {

  public SubstantiveParametersTagService() {
    super(ISubstantiveParametersTagDtoService.class);
  }

  @Override
  public List<SubstantiveParametersTagDto> listDto(SubstantiveParametersTagDtoQuery query) {
    return super.listDto(query);
  }

  @Override
  public PageModel<SubstantiveParametersTagDto> listDtoPaged(SubstantiveParametersTagDtoQuery query) {
    return super.listDtoPaged(query);
  }
}
