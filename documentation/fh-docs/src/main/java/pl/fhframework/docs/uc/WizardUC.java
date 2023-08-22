package pl.fhframework.docs.uc;

import pl.fhframework.core.designer.IDocumentationUseCase;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.docs.forms.component.WizardForm;
import pl.fhframework.docs.forms.component.model.WizardElement;
import pl.fhframework.annotations.Action;
import pl.fhframework.events.BreakLevelEnum;


/**
 * Use case supporting Wizard documentation
 */
@UseCase
public class WizardUC implements IDocumentationUseCase<WizardElement> {
    private WizardElement model;

    @Override
    public void start(WizardElement model) {
        this.model = model;
        showForm(WizardForm.class, model);
    }


    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void showOptionalStep() {
        model.setShowOptionalTab(true);
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void hideOptionalStep() {
        model.setShowOptionalTab(false);
    }

}

