package pl.fhframework.docs.uc;

import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.core.FhSubsystemException;
import pl.fhframework.core.designer.ComponentElement;
import pl.fhframework.core.designer.DocumentedAttribute;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.security.annotations.SystemFunction;
import pl.fhframework.core.uc.IInitialUseCase;
import pl.fhframework.core.uc.IUseCaseNoCallback;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.docs.DocsSystemFunction;
import pl.fhframework.docs.forms.ComponentsListForm;
import pl.fhframework.docs.forms.component.model.RadioOptionElement;
import pl.fhframework.annotations.Action;
import pl.fhframework.events.BreakLevelEnum;
import pl.fhframework.model.forms.docs.FormComponentsDocumentationService;
import pl.fhframework.model.forms.docs.model.FormComponentDocumentationHolder;
import pl.fhframework.model.forms.messages.Messages;

import java.util.Collections;
import java.util.Random;

@UseCase
@UseCaseWithUrl(alias = "docs-form-components")
//@SystemFunction(DocsSystemFunction.FH_DOCUMENTATION_VIEW)
public class FormComponentsDocumentationUC implements IInitialUseCase {

    //private static final String FORM_CLASS_PATH = "pl.fhframework.docs.forms.component.%s%sForm";
    //private static final String ELEMENT_CLASS_PATH = "pl.fhframework.docs.forms.component.%smodel.%sElement";
    //private static final String FH_BASIC_COMPONENTS_PACKAGE = "pl.fhframework.model";

    private FormComponentDocumentationHolder formComponent = new FormComponentDocumentationHolder();
    private ComponentElement selectedElement;

    @Autowired
    private FormComponentsDocumentationService documentationService;

    @Override
    public void start() {
        try {
            formComponent = documentationService.findDocumentedBasicComponentsForPredicate(null);
            showForm(ComponentsListForm.class, formComponent);
        } catch (ClassNotFoundException exception) {
            FhLogger.error(exception);
            throw new FhSubsystemException("Error during loading forms for documentation!");
        }
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void display(ComponentElement selectedElement) {
        if (selectedElement != null) {
            //setDefaultClickedElement(selectedElement);
            this.selectedElement = selectedElement;

            if (selectedElement.getUseCase() != null) {
                // run use case
                runUseCase(selectedElement.getUseCase(), selectedElement, new IUseCaseNoCallback() {});
            } else {
                // just show the form
                showForm(selectedElement.getForm(), selectedElement);
            }
        }
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void onChangeExample() {
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void backToFormComponentsList() {
        showForm(ComponentsListForm.class, formComponent);
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void onAttributeSelect() {
        DocumentedAttribute selectedBasicAttribute = this.selectedElement.getSelectedBasicAttribute();
        if (!selectedBasicAttribute.getNestedAttributes().isEmpty()) {
            this.selectedElement.setNestedAttributes(selectedBasicAttribute.getNestedAttributes());
        } else {
            this.selectedElement.setNestedAttributes(Collections.emptyList());
        }
    }

    @Action
    public void akcjaUC() {
        Messages.showInfoMessage(this.getUserSession(), "Wciśnięto przycisk.");
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void onRandom(RadioOptionElement roe) {
        switch (new Random().nextInt(4)) {
            case 0:
                roe.setSelectCountry(roe.getRadioGhana());
                break;
            case 1:
                roe.setSelectCountry(roe.getRadioNigeria());
                break;
            case 2:
                roe.setSelectCountry(roe.getRadioSenegal());
                break;
            case 3:
                roe.setSelectCountry(roe.getRadioZimbabwe());
                break;
        }
    }
}
