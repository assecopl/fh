package pl.fhframework.docs.core.keyboard;

import pl.fhframework.core.security.annotations.SystemFunction;
import pl.fhframework.core.uc.IInitialUseCase;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.docs.DocsSystemFunction;
import pl.fhframework.annotations.Action;
import pl.fhframework.model.forms.messages.Messages;

@UseCase
@UseCaseWithUrl(alias = "docs-keyboard-events")
//@SystemFunction(DocsSystemFunction.FH_DOCUMENTATION_VIEW)
public class KeyboardEventsUC implements IInitialUseCase {

    @Override
    public void start() {
        showForm(KeyboardEventsForm.class, null);
    }

    @Action(immediate = true)
    void keyPressedESC() {
        Messages.showMessage(getUserSession(), "Key pressed", "You pressed ESC", Messages.Severity.INFO);
    }

    @Action(immediate = true)
    void keyPressedEnter() {
        Messages.showMessage(getUserSession(), "Key pressed", "You pressed ENTER", Messages.Severity.INFO);
    }

    @Action(immediate = true)
    void keyPressedCtrlX() {
        Messages.showMessage(getUserSession(), "Key pressed", "You pressed CTRL+X", Messages.Severity.INFO);
    }

    @Action(immediate = true)
    void keyPressed() {
        Messages.showMessage(getUserSession(), "Key pressed", "You pressed the defined key", Messages.Severity.INFO);
    }
}
