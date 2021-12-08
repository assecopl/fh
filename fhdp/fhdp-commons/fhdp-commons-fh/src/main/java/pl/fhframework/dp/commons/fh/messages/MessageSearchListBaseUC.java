package pl.fhframework.dp.commons.fh.messages;

import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.dp.commons.fh.outline.OutlineService;
import pl.fhframework.dp.commons.fh.services.MessageSearchService;
import pl.fhframework.dp.commons.fh.uc.GenericListUC;
import pl.fhframework.dp.transport.msg.MessageDto;
import pl.fhframework.dp.transport.msg.MessageDtoQuery;
import pl.fhframework.annotations.Action;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.model.forms.messages.Messages;

import java.util.List;

public abstract class MessageSearchListBaseUC<MODEL extends MessageSearchListModel> extends GenericListUC<MODEL, MessageSearchHandlingUC.Params, MessageDto> {

    protected MODEL messageSearchListModel;

    @Autowired
    private MessageSearchService messageSearchService;

    @Autowired
    private MessageSearchOutlineService messageOutlineService;

    @Autowired
    private OutlineService outlineService;

    @Override
    public void start() {
        super.declarationBars(false);
        super.start();

        showForm(getSearchFormId(), getListData());
        showForm(getSearchButtonsFormId(), getListData());
        this.clearQuery();
    }

    @Override
    protected void readData() {
        try {
            MessageDtoQuery query = messageSearchListModel.getSearchModel().getQuery();
            getListData().setList(messageSearchService.listDtoPaged(query));;
        } catch (Exception e) {
            e.printStackTrace();
            FhLogger.error(e);
            Messages.showErrorMessage(getUserSession(), "Error getting data", e);
        }
    }

    @Override
    protected String getListFormId() {
        return "pl.fhframework.dp.commons.fh.messages.MessageSearchListForm";
    }

    protected String getSearchFormId() {
        return "pl.fhframework.dp.commons.fh.messages.MessageSearchCTListSearch";
    }

    protected String getSearchButtonsFormId() {
        return "pl.fhframework.dp.commons.fh.declaration.list.DeclarationCTListSearchButtons";
    }

    protected String getSearchDetailsForm() {
        return "pl.fhframework.dp.commons.fh.messages.MessageSearchByDetailsForm";
    }

    @Override
    @Action("search")
    public void search() {
        super.advancedSidebarManagement(true);
        MessageDtoQuery query = messageSearchListModel.getQuery();
        this.messageSearchListModel.getSearchModel().setQuery(query);
        getListData().setList(messageSearchService.listDtoPaged(query));
        getListData().setSearchTemplateBuilderModel(messageSearchListModel.getSearchTemplateBuilderModel());

        super.search();
    }

    @Override
    @Action(validate = false)
    public void clearQuery() {
        getListData().setQuery(new MessageDtoQuery());
        messageSearchListModel.setQuery(getListData().getQuery());
        messageSearchListModel.getSearchModel().setQuery(getListData().getQuery());
        messageSearchListModel.setSearchModel(new MessageSearchModel(new MessageDtoQuery()));
    }

    @Action
    public void openSearchByDetails(){
        showForm(getSearchDetailsForm(), getListData());
        super.toogleAdvancedSideBar();
    }

    @Action
    public void closeSearchByDetails(){
        super.toogleAdvancedSideBar();
    }

    @Action(validate = false)
    public void cancel() {
        super.appSiderDetailsManagement(true);
    }

    @Override
    public void close() {
        super.sideBarManagement(true);
        super.searchButtonsManagement(true);
        super.advancedSidebarManagement(true);
        super.close();
    }

    @Override
    protected MessageSearchHandlingUC.Params getParamsForNew() {
        return this.getParams();
    }

    @Override
    protected MessageSearchHandlingUC.Params getParamsForEdit() {
        return this.getParams();
    }

    private MessageSearchHandlingUC.Params getParams() {
        MessageSelectedOutline messagesLeftMenu = getMessageTreeElements();
        String selectLabel = messageOutlineService.generateMessageTreeElementName(messageSearchListModel.getSelectedMessage());
        return new MessageSearchHandlingUC.Params(messagesLeftMenu.getLeftMenuElements(), selectLabel);
    }

    public MessageSelectedOutline getMessageTreeElements() {
        MessageDtoQuery query = this.getQueryMessageTreeElements();
        List<MessageDto> messagesDtoList = this.messageSearchService.listDto(query);
        return this.messageOutlineService.buildMessagesTreeMenu(messagesDtoList, this.messageSearchListModel.getSelectedMessage().getRepositoryId());
    }

    public MessageDtoQuery getQueryMessageTreeElements() {
        MessageDtoQuery query = new MessageDtoQuery();
        query.setDeclarationId(this.messageSearchListModel.getSelectedMessage().getDeclarationId());

        return query;
    }

    @Override
    protected Class getEditFormClass() {
        return MessageSearchHandlingUC.class;
    }
}
