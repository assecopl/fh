package pl.fhframework.docs.application.url;

import pl.fhframework.annotations.Action;
import pl.fhframework.core.security.annotations.SystemFunction;
import pl.fhframework.core.uc.ICommunicationUseCase;
import pl.fhframework.core.uc.IInitialUseCase;
import pl.fhframework.core.uc.Parameter;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.docs.DocsSystemFunction;
import pl.fhframework.model.forms.messages.Messages;

/**
 * Use case for documentation of returning to FH from external website.
 */
@UseCase
@UseCaseWithUrl(alias = "docs-external-return")
//@SystemFunction(DocsSystemFunction.FH_DOCUMENTATION_VIEW)
public class ExternalReturnUC implements IInitialUseCase, ICommunicationUseCase {

    @Override
    public void start() {
        showForm(ExternalReturnForm.class, null);
    }

    @Action(value = "name", remote = true)
    void onNameEvent(@Parameter(name = "name", comment = "What's your name?") String name) {
        Messages.showInfoMessage(getUserSession(), String.format("'name' event was called"));
    }

    @Action(value = "passName", remote = true)
    void onPassNameEvent(@Parameter(name = "passName", comment = "What's your name?") String name) {
        Messages.showInfoMessage(getUserSession(), String.format("'passName' event was called with String value = %s", name));
    }

}