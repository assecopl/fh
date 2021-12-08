package pl.fhframework.dp.commons.fh.declaration.message;

import pl.fhframework.dp.commons.fh.messages.MessageSearchListBaseUC;
import pl.fhframework.dp.commons.fh.messages.MessageSearchListModel;
import pl.fhframework.dp.transport.msg.MessageDtoQuery;
import pl.fhframework.core.uc.UseCase;

@UseCase
public class ExampleMessageSearchListUC extends MessageSearchListBaseUC<MessageSearchListModel> {

    @Override
    protected MessageSearchListModel initInternalListData() {
        this.messageSearchListModel = new MessageSearchListModel();
        MessageDtoQuery query = new MessageDtoQuery();

        query.setResultHidden(true);
        this.messageSearchListModel.setQuery(query);
        this.messageSearchListModel.getSearchModel().setQuery(query);

        return this.messageSearchListModel;
    }
}
