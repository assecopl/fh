package pl.fhframework.dp.commons.fh.services;

import pl.fhframework.dp.commons.fh.utils.rest.facade.GenericFacadeService;
import pl.fhframework.dp.transport.searchtemplate.SearchTemplateDto;
import pl.fhframework.dp.transport.searchtemplate.SearchTemplateDtoQuery;
import pl.fhframework.core.services.FhService;
@FhService
public class SearchTemplateService extends GenericFacadeService<Long, SearchTemplateDto, SearchTemplateDto, SearchTemplateDtoQuery> {

    public SearchTemplateService() {
        super(ISearchTemplateDtoService.class);
    }

    @Override
    public Long persistDto(SearchTemplateDto dto){
        return super.persistDto(dto);
    }

    @Override
    public Long listCount(SearchTemplateDtoQuery query) {
        return super.listCount(query);
    }
}
