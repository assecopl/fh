package pl.fhframework.validation;

import pl.fhframework.SessionManager;
import pl.fhframework.core.FhException;
import pl.fhframework.model.PresentationStyleEnum;
import pl.fhframework.model.forms.Form;

/**
 * Created by pawel.ruta on 2017-09-25.
 */
public class ValidationRuleBase {
    public static String VALIDATION_MSG_PREFIX = "validationMessages";

    private static final ThreadLocal<IValidationResults> validationResultsTl = new ThreadLocal<>();

    private static final ThreadLocal<Form> formTl = new ThreadLocal<>();

    public static void setValidationResults(IValidationResults validationResults) {
        if (validationResults == null) {
            validationResultsTl.remove();
        }
        else {
            validationResultsTl.set(validationResults);
        }
    }

    public static void setForm(Form form) {
        if (form == null) {
            formTl.remove();
        }
        else {
            formTl.set(form);
        }
    }

    public IValidationResults getValidationResults() {
        return validationResultsTl.get();
    }

    public Form getForm() {
        return formTl.get();
    }

    protected void addCustomMessage(String attributeName, String message, PresentationStyleEnum presentationStyleEnum) {
        addCustomMessage(null, attributeName, message, presentationStyleEnum);
    }

    protected void addCustomMessage(Object parent, String attributeName, String message, PresentationStyleEnum presentationStyleEnum) {
        IValidationResults validationResults = getValidationResults();
        if (validationResults == null) {
            validationResults = SessionManager.getUserSession().getValidationResults();
        }
        Form form = getForm();
        if (form == null) {
            form = SessionManager.getUserSession().getUseCaseContainer().getCurrentUseCaseContext().getUseCase().getActiveForm();
        }
        if (parent == null) {
            if (form != null) {
                parent = form.getModel();
            } else {
                parent = "root";
            }
        }
        validationResults.addCustomMessage(parent, attributeName, message, presentationStyleEnum);
    }
}
