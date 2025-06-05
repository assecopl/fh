package pl.fhframework.docs.pml;

import pl.fhframework.core.security.annotations.SystemFunction;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.IInitialUseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.docs.DocsSystemFunction;

@UseCase
@UseCaseWithUrl(alias = "docs-fhml")
//@SystemFunction(DocsSystemFunction.FH_DOCUMENTATION_VIEW)
public class PMLDocumentationUC implements IInitialUseCase {
    private PMLDocumentationModel model = new PMLDocumentationModel();

    @Override
    public void start() {
        showForm(PMLDocumentationForm.class, model);
    }
}
