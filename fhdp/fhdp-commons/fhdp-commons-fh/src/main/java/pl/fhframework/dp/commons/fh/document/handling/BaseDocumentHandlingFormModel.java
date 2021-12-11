package pl.fhframework.dp.commons.fh.document.handling;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.dp.commons.fh.outline.ElementCT;
import pl.fhframework.dp.commons.fh.outline.TreeElement;
import pl.fhframework.dp.transport.dto.operations.OperationDtoQuery;
import pl.fhframework.dp.transport.enums.NullableBooleanEnum;
import pl.fhframework.dp.transport.enums.PerformerEnum;
import pl.fhframework.helper.AutowireHelper;
import pl.fhframework.model.forms.AccessibilityEnum;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public abstract class BaseDocumentHandlingFormModel<DTO, DOC, SUBMODEL extends BaseDocumentFormModel<DTO, DOC>> {
    @Autowired
    protected MessageService messageService;

    @Value("${model.enums.package:}")
    protected String enumsPackage;

    public BaseDocumentHandlingFormModel() {
        AutowireHelper.autowire(this, messageService);
    }

//    private PageModel<OperationCT> operationPageModel;
//    private OperationCT selectedOperation;
//    private OperationCT currentOperation;
    private List<TreeElement<ElementCT>> docLeftMenu;
    private String searchParam;
    private Map<String, String> searchMap;
    private List<String> searchPointers;
    private SUBMODEL specificDocFormModel;
    private LocalDate referenceDate = LocalDate.now();
    private boolean previewMode = false;
    @Value("${fhdp.pinup.url:none}")
    private String pinupUrl;

    private int activeOutlineTabIndex=0;
    private int activeTabIndex=0;
    private int historyTabIndex=1;
    private int messagesTabIndex=0;
    private int timerTimeout=0;

    //TODO: move to successors
//    private List<DocMessageDto> messages;
//    private List<TreeElement<DocMessageDto>> declarationMessagesLeftMenu;

    OperationDtoQuery operationQuery = new OperationDtoQuery();
    private String spacer = "<hr>";
    private List<NullableBooleanEnum> booleanEnum = Arrays.asList(NullableBooleanEnum.values());
    private List<PerformerEnum> performerEnum = Arrays.asList(PerformerEnum.values());

    //TODO: move to descendants
//    public String getSubPageName() {
//        String point = " [unescape='8226'] ";
//        if("History".equals(this.getSpecificDocFormModel().getVariant())) {
//            return point.concat(messageService.getAllBundles().getMessage("declaration.ct.operations.history.table.label"));
//        } else if("Messages".equals(this.getSpecificDocFormModel().getVariant())){
//            return point.concat(messageService.getAllBundles().getMessage("message.content.title"));
//        }
//        return "";
//    }

    @Setter
    private IDocumentHandler documentHandler;

    public AccessibilityEnum getOperationAccessibility(String opCode) {
        if(documentHandler != null) {
            return documentHandler.getOperationAccessibility(opCode);
        } else {
            return AccessibilityEnum.EDIT;
        }
    }

    //TODO: move to successors!!!
//    public String translateOperationCodeEnum() {
//        if(this.getSelectedOperation() == null) return "";
//        String value = this.getSelectedOperation().getCode();
//        if(enumsPackage.isEmpty()){
//            return value;
//        }
//        return translateOperationCodeEnum(value);
//    }

//    public String translateOperationCodeEnum(String value) {
//        if(enumsPackage.isEmpty()){
//            return value;
//        }
//        String operationProp = String.format("enum.%s.OperationCodeEnum.%s", enumsPackage, value);
//        return messageService.getAllBundles().getMessage(operationProp);
//    }
//
//    public Boolean isOperationLabels() {
//        boolean isOperationsPending = Boolean.parseBoolean(FhUtils.getCookieByKey("operationPending"));
//        if(!isOperationsPending)
//            return Boolean.parseBoolean(FhUtils.getCookieByKey("operationLabels"));
//
//        return false;
//    }

    //TODO: move to successors!!!
//    public DTO getDeclarationCTDto() {
//        if(specificDeclarationFormModel == null) return null;
//        return specificDeclarationFormModel.getEntity();
//    }

    public abstract Long getDocId();


    public AccessibilityEnum isFallbackVisible() {
        if (isFallback()) {
            return AccessibilityEnum.EDIT;
        } else {
            return AccessibilityEnum.HIDDEN;
        }
    }

    protected abstract boolean isFallback();

    protected abstract String getDocumentState();

    public String highlightFindedElement(ElementCT el) {
        if (el == null) {
            return "";
        }

        if (searchPointers == null || !searchPointers.contains(el.getId())) {
            return el.getLabel();
        }
        return "[mark]"+ el.getLabel() +"[/mark]";
    }

    public abstract void setDocId(Long id);

    public abstract DOC getDocument();

    public abstract DOC getPrevDocument();
    public abstract void setPrevDocument(DOC document);

    public abstract void setProcessId(String processID);

    public abstract String getProcessId();
}
