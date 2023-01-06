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

    private List<TreeElement<ElementCT>> docLeftMenu;
    private String searchParam;
    private Map<String, String> searchMap;
    private List<String> searchPointers;
    private SUBMODEL specificDocFormModel;
    private LocalDate referenceDate = LocalDate.now();
    private boolean previewMode = false;
    @Value("${fhdp.pinup.url:none}")
    private String pinupUrl;
    @Value("${fhdp.pinup.password:none}")
    private String pinupCypherPassword;

    private int activeOutlineTabIndex=0;
    private int activeTabIndex=0;
    private int historyTabIndex=1;
    private int messagesTabIndex=0;
    private int timerTimeout=0;

    OperationDtoQuery operationQuery = new OperationDtoQuery();
    private String spacer = "<hr>";
    private List<NullableBooleanEnum> booleanEnum = Arrays.asList(NullableBooleanEnum.values());
    private List<PerformerEnum> performerEnum = Arrays.asList(PerformerEnum.values());


    @Setter
    private IDocumentHandler documentHandler;

    public AccessibilityEnum getOperationAccessibility(String opCode) {
        if(documentHandler != null) {
            return documentHandler.getOperationAccessibility(opCode);
        } else {
            return AccessibilityEnum.EDIT;
        }
    }

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
