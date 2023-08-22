package pl.fhframework.docs.scheduler;

import pl.fhframework.core.security.annotations.SystemFunction;
import pl.fhframework.core.uc.IInitialUseCase;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.docs.DocsSystemFunction;

@UseCase
@UseCaseWithUrl(alias = "docs-running")
//@SystemFunction(DocsSystemFunction.FH_DOCUMENTATION_VIEW)
public class PeriodicJobsDocumentationUC implements IInitialUseCase {

    @Override
    public void start() {
        showForm(PeriodicJobsDocumentationForm.class, null);
    }
}