package pl.fhframework.dp.commons.fh.declaration.handling;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.dp.commons.fh.outline.ElementCT;
import pl.fhframework.dp.commons.fh.outline.TreeElement;
import pl.fhframework.dp.transport.dto.declaration.OperationDtoQuery;
import pl.fhframework.dp.transport.enums.NullableBooleanEnum;
import pl.fhframework.dp.transport.enums.PerformerEnum;
import pl.fhframework.dp.transport.msg.DeclarationMessageDto;
import pl.fhframework.helper.AutowireHelper;
import pl.fhframework.model.forms.AccessibilityEnum;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public abstract class BaseDeclarationHandlingFormModel<DTO, DECL, SUBMODEL extends BaseDeclarationFormModel<DTO, DECL>> {
    @Autowired
    protected MessageService messageService;

    @Value("${model.enums.package:}")
    protected String enumsPackage;

    public BaseDeclarationHandlingFormModel() {
        AutowireHelper.autowire(this, messageService);
    }

//    private PageModel<OperationCT> operationPageModel;
//    private OperationCT selectedOperation;
//    private OperationCT currentOperation;
    private List<TreeElement<ElementCT>> declarationLeftMenu;
    private String searchParam;
    private Map<String, String> searchMap;
    private List<String> searchPointers;
    private SUBMODEL specificDeclarationFormModel;
    private LocalDate referenceDate = LocalDate.now();
    private boolean previewMode = false;
    @Value("${fhdp.pinup.url:none}")
    private String pinupUrl;

    private int activeOutlineTabIndex=0;
    private int activeTabIndex=0;
    private int historyTabIndex=1;
    private int messagesTabIndex=0;
    private int timerTimeout=0;

    private List<DeclarationMessageDto> messages;
    private List<TreeElement<DeclarationMessageDto>> declarationMessagesLeftMenu;

    OperationDtoQuery operationQuery = new OperationDtoQuery();
    private String spacer = "<hr>";
    private List<NullableBooleanEnum> booleanEnum = Arrays.asList(NullableBooleanEnum.values());
    private List<PerformerEnum> performerEnum = Arrays.asList(PerformerEnum.values());

    public String getSubPageName() {
        String point = " [unescape='8226'] ";
        if("History".equals(this.getSpecificDeclarationFormModel().getVariant())) {
            return point.concat(messageService.getAllBundles().getMessage("declaration.ct.operations.history.table.label"));
        } else if("Messages".equals(this.getSpecificDeclarationFormModel().getVariant())){
            return point.concat(messageService.getAllBundles().getMessage("message.content.title"));
        }
        return "";
    }

    @Setter
    private IDeclarationHandler declarationHandler;

    public AccessibilityEnum getOperationAccessibility(String opCode) {
        if(declarationHandler != null) {
            return declarationHandler.getOperationAccessibility(opCode);
        } else {
            return AccessibilityEnum.EDIT;
        }
    }

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

    public DTO getDeclarationCTDto() {
        if(specificDeclarationFormModel == null) return null;
        return specificDeclarationFormModel.getEntity();
    }

    public abstract Long getDeclarationId();


    public AccessibilityEnum isFallbackVisible() {
        if (isFallback()) {
            return AccessibilityEnum.EDIT;
        } else {
            return AccessibilityEnum.HIDDEN;
        }
    }

    protected abstract boolean isFallback();

    protected abstract String getDeclarationState();

    public String highlightFindedElement(ElementCT el) {
        if (el == null) {
            return "";
        }

        if (searchPointers == null || !searchPointers.contains(el.getId())) {
            return el.getLabel();
        }
        return "[mark]"+ el.getLabel() +"[/mark]";
    }

    public abstract void setDeclarationId(Long id);

    public abstract DECL getDeclaration();

    public abstract DECL getPrevDeclaration();
    public abstract void setPrevDeclaration(DECL declaration);

    public abstract void setProcessId(String processID);

    public abstract String getProcessId();
}
