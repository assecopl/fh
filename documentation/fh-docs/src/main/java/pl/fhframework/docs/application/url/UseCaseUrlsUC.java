package pl.fhframework.docs.application.url;

import pl.fhframework.core.security.annotations.SystemFunction;
import pl.fhframework.core.uc.IInitialUseCase;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.docs.DocsSystemFunction;
import pl.fhframework.docs.application.management.ApplicationModesForm;
import pl.fhframework.docs.application.management.ApplicationModesModel;

/**
 * Use case URL documentation UC.
 */
@UseCase
@UseCaseWithUrl(alias = "docs-urls")
//@SystemFunction(DocsSystemFunction.FH_DOCUMENTATION_VIEW)
public class UseCaseUrlsUC implements IInitialUseCase {

    @Override
    public void start() {
        showForm(UseCaseUrlsForm.class, null);
    }
}
