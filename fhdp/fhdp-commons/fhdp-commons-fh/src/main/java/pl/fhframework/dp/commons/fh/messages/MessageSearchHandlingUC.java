package pl.fhframework.dp.commons.fh.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.dp.commons.fh.outline.TreeElement;
import pl.fhframework.dp.commons.fh.services.DeclarationMessageService;
import pl.fhframework.dp.commons.fh.uc.FhdpBaseUC;
import pl.fhframework.dp.commons.fh.uc.IGenericListOutputCallback;
import pl.fhframework.dp.transport.dto.commons.GetMessageOperationResultBaseDto;
import pl.fhframework.dp.transport.dto.commons.OperationGetMessageDto;
import pl.fhframework.dp.transport.msg.DeclarationMessageDto;
import pl.fhframework.dp.transport.msg.MessageDto;
import pl.fhframework.annotations.Action;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.uc.IUseCaseOneInput;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.event.EventRegistry;
import pl.fhframework.model.forms.Form;
import pl.fhframework.model.forms.TreeElementFhDP;
import pl.fhframework.model.forms.TreeFhDP;

import java.nio.charset.StandardCharsets;
import java.util.List;

@UseCase
public class MessageSearchHandlingUC extends FhdpBaseUC implements IUseCaseOneInput<MessageSearchHandlingUC.Params, IGenericListOutputCallback<MessageHandlingFormModel>> {

    @Autowired
    private GetMessageService getMessageService;

    @Autowired
    private DeclarationMessageService declarationMessageService;

    @Autowired
    private EventRegistry eventRegistry;

    protected MessageHandlingFormModel model;

    private String labelSel;

    private boolean firstInit = true;

    protected Form form;

    @Override
    public void start(MessageSearchHandlingUC.Params params) {
        try {
            model = new MessageHandlingFormModel();
            model.setSelectMessagesLeftMenu(params.getMessagesLeftMenu());

            super.declarationBars(false);
            super.searchButtonsManagement(true);

            clickSelectTreeElement(params.getMessagesLeftMenu().get(0));

            showForm(getSearchButtonsFormId(), this.model);
            showForm(this.getSearchDetailId(), this.model);
            form = showForm(this.getSearchDetailLeftId(), this.model);

            labelSel = params.getSelectedLabel();
        } catch (BeansException e) {
            FhLogger.error("{}{}", e.getMessage(), e);
        }
    }

    public void openTreeBranch(String treeId, String elementId) {
        String input = String.format("{\"parentId\": \"%s\", \"nestionId\": \"%s\"}", treeId, elementId);
        eventRegistry.fireCustomActionEvent("openTreeBranch", input);
    }

    public String getSearchDetailId() { return "pl.fhframework.dp.commons.fh.messages.MessageHandlingForm"; }

    public String getSearchDetailLeftId() { return "pl.fhframework.dp.commons.fh.messages.MessageSearchDetailLeftForm"; }

    public String getSearchButtonsFormId() { return "pl.fhframework.dp.commons.fh.messages.MessageHandlingButtonsForm"; }

    @Action("onClickTreeElement")
    public void onClickTreeElement(TreeElement<MessageDto> element) {
        if(firstInit) {
            TreeFhDP tree = (TreeFhDP)form.getFormElement("messageSearchTreeLeftMenu");
            TreeElementFhDP treeElement = MessageSearchOutlineService.findTreeElementByLabel(tree, labelSel);
            firstInit = false;
            assert treeElement != null;
            openTreeBranch("messageSearchTreeLeftMenu", treeElement.getId());
        } else {
            clickSelectTreeElement(element);
        }
    }

    private void clickSelectTreeElement(TreeElement<MessageDto> element) {
        this.setMessageContent(element.getObj());
        this.model.setSelectedMessageTreeElement(element);
        showForm(this.getSearchDetailId(), this.model);
    }

    public void setMessageContent(MessageDto messageDto) {
        OperationGetMessageDto dto = new OperationGetMessageDto();
        dto.setRepositoryId(messageDto.getRepositoryId());
        GetMessageOperationResultBaseDto messageResultDto = getMessageService.performOperation(dto);
        if(messageResultDto.getDocument() != null) {
            messageDto.setContent(new String(messageResultDto.getDocument().getContent(), StandardCharsets.UTF_8));
        }
    }

    @Action(value = "onTabChange", validate = false)
    public void onTabChange() {
        super.buttonsFormManagement(model.getActiveTabIndex() != 2);
    }

    @Action
    public void close() {
        super.buttonsFormManagement(true);
        super.searchButtonsManagement(false);
        exit().cancel();
    }

    @Action
    public void downloadDeclarationMessage(MessageDto messageDto) {
        DeclarationMessageDto declarationMessageDto = new DeclarationMessageDto();
        declarationMessageDto.setMessageContent(messageDto.getContent());
        declarationMessageService.downloadDeclarationMessageFile(declarationMessageDto, eventRegistry);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Params {
        List<TreeElement<MessageDto>> messagesLeftMenu;
        String selectedLabel;
    }
}
