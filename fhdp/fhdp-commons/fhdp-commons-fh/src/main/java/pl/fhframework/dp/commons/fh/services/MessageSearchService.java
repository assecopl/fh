package pl.fhframework.dp.commons.fh.services;

import pl.fhframework.dp.commons.fh.utils.rest.facade.GenericFacadeService;
import pl.fhframework.dp.transport.msg.MessageDto;
import pl.fhframework.dp.transport.msg.MessageDtoQuery;
import pl.fhframework.dp.transport.service.IMessageDtoService;
import pl.fhframework.core.services.FhService;
import pl.fhframework.model.forms.PageModel;

import java.util.List;

@FhService
public class MessageSearchService extends GenericFacadeService<Long, MessageDto, MessageDto, MessageDtoQuery> {

    public MessageSearchService() {
        super(IMessageDtoService.class);
    }

    @Override
    public List<MessageDto> listDto(MessageDtoQuery query) {
        return super.listDto(query);
    }

    @Override
    public PageModel<MessageDto> listDtoPaged(MessageDtoQuery query) {return super.listDtoPaged(query);}
}
