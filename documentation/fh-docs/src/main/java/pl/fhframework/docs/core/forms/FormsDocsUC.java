package pl.fhframework.docs.core.forms;

import pl.fhframework.core.security.annotations.SystemFunction;
import pl.fhframework.core.uc.IInitialUseCase;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.docs.DocsSystemFunction;
import pl.fhframework.docs.core.forms.example.OtherModal;
import pl.fhframework.docs.core.forms.example.SimpleModal;
import pl.fhframework.annotations.Action;
import pl.fhframework.model.forms.messages.ActionButton;
import pl.fhframework.model.forms.messages.Messages;

@UseCase
@UseCaseWithUrl(alias = "docs-forms")
//@SystemFunction(DocsSystemFunction.FH_DOCUMENTATION_VIEW)
public class FormsDocsUC implements IInitialUseCase {

    private FormsDocsModel model;

    @Override
    public void start() {
        model = new FormsDocsModel();
        showForm(FormsDocs.class, model);
    }

    @Action
    void showMessage() {
        ActionButton button = ActionButton.get("Next modal", v -> {
            Messages.showInfoMessage(getUserSession(), "Modal with overflow");
        });
        ActionButton noBtn = ActionButton.getClose("No");

        Messages.showActionMessage(getUserSession(), "Title", "Do you want next modal?", Messages.Severity.WARNING, button, noBtn);
    }

    @Action
    void showSimpleModal() {
        showForm(SimpleModal.class, model);
    }

    @Action
    void showOtherModal() {
        model.setDisplaySameModalType(false);
        showForm(OtherModal.class, model);
    }

    @Action
    void showSameModal() {
        model.setDisplaySameModalType(true);
        showForm(OtherModal.class, model);
    }

    @Action
    void showMessageWithModal() {
        ActionButton button = ActionButton.get("Next modal?", v -> {
            if(model.isDisplaySameModalType()) {
                showOtherModal();
            } else {
                showSimpleModal();
            }
        });
        ActionButton noBtn = ActionButton.getClose("No");

        Messages.showActionMessage(getUserSession(), "Title", "Do you want next modal, but not with overflow?", Messages.Severity.WARNING, button, noBtn);
    }

    /**
     * "showForm" is just for testing or when UseCase is developed in designer.
     * Normally dev should call "hideForm" with reference on given form
     */
    @Action
    void close() {
//        hideForm();
        showForm(FormsDocs.class, model);
    }
}
