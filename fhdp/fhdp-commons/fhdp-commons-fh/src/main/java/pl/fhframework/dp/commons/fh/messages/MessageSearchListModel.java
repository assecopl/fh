package pl.fhframework.dp.commons.fh.messages;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.dp.commons.fh.declaration.list.searchtemplate.SearchTemplateBuilderModel;
import pl.fhframework.dp.commons.fh.model.GenericListPagedModel;
import pl.fhframework.dp.commons.fh.outline.TreeElement;
import pl.fhframework.dp.transport.drs.repository.OtherMetadata;
import pl.fhframework.dp.transport.msg.MessageDto;
import pl.fhframework.dp.transport.msg.MessageDtoQuery;
import pl.fhframework.core.model.Model;
import pl.fhframework.model.forms.PageModel;

import java.util.Optional;

@Model
@Getter
@Setter
public class MessageSearchListModel extends GenericListPagedModel<MessageDto, MessageDtoQuery> {

    @Override
    public PageModel<MessageDto> getList() {
        return super.getList();
    }

    @Override
    public void setList(PageModel<MessageDto> list) {
        super.setList(list);
    }

    @Override
    public MessageDto getSelectedElement() {
        return super.getSelectedElement();
    }

    @Override
    public void setSelectedElement(MessageDto selectedElement) {
        super.setSelectedElement(selectedElement);
    }

    @Override
    public MessageDtoQuery getQuery() {
        return super.getQuery();
    }

    @Override
    public void setQuery(MessageDtoQuery query) {
        super.setQuery(query);
    }

    private MessageSearchModel searchModel = new MessageSearchModel(getQuery());
    private SearchTemplateBuilderModel searchTemplateBuilderModel;
    private PageModel<TreeElement<MessageDto>> messagesPageModel;
    private MessageDto selectedMessage;

    public String getDataFromOtherMetadata(MessageDto messageDto, String key) {

        OtherMetadata otherMetadata = messageDto.getMetadata().getOtherMetadata()
            .stream()
            .filter(el -> el.getName().equals(key))
            .findFirst().orElse(new OtherMetadata());

        return Optional.ofNullable(otherMetadata.getValue()).orElse("");
    }

    public String getBaseMessageIdentifier(MessageDto messageDto) {
        return Optional.ofNullable(messageDto.getMetadata().getLocalReferenceNumber()).orElse(
            Optional.ofNullable(messageDto.getMetadata().getMessageIdentification()).orElse(""));
    }

    public String getMessageDateStoredByOneView(MessageDto messageDto) {
        if(messageDto.getDelivered() != null) {
            return "";
        }

        return messageDto.getStored().toString();
    }

}
