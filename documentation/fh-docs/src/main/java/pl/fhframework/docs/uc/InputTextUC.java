package pl.fhframework.docs.uc;

import pl.fhframework.core.designer.IDocumentationUseCase;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.docs.forms.component.InputTextForm;
import pl.fhframework.docs.forms.component.model.InputTextElement;
import pl.fhframework.annotations.Action;
import pl.fhframework.events.BreakLevelEnum;
import pl.fhframework.model.PresentationStyleEnum;


/**
 * Use case supporting InputText documentation
 */
@UseCase
public class InputTextUC implements IDocumentationUseCase<InputTextElement> {
    private InputTextElement model;

    @Override
    public void start(InputTextElement model) {
        this.model = model;
        showForm(InputTextForm.class, model);
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void onInputExample() {
        FhLogger.debug(this.getClass(), logger -> logger.log("Hello onInputExample event example."));
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void onChangeExample() {
        FhLogger.debug(this.getClass(), logger -> logger.log("Hello onChange event example."));
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void convertXmlToXmlElement() {
        InputTextElement inputTextElement = model;
        inputTextElement.setXml(inputTextElement.getXml()
                .replace("{", "\\{")
                .replace("}", "\\}")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("\n", "&#13;")
                .replace("\r", "")
                .replace("\t", "&#8194;")
                .replace(" ", "&#32;"));
    }

    @Action
    public void onOk() {
        getFieldsHighlightingList().add(model, "highlight", PresentationStyleEnum.OK);
    }

    @Action
    public void onInfo() {
        getFieldsHighlightingList().add(model, "highlight", PresentationStyleEnum.INFO);
    }

    @Action
    public void onWarning() {
        getFieldsHighlightingList().add(model, "highlight", PresentationStyleEnum.WARNING);
    }

    @Action
    public void onError() {
        getFieldsHighlightingList().add(model, "highlight", PresentationStyleEnum.ERROR);
    }
}
