package pl.fhframework.docs.layouting.elementsLayout;

import pl.fhframework.core.security.annotations.SystemFunction;
import pl.fhframework.core.uc.IInitialUseCase;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.docs.DocsSystemFunction;

@UseCase
@UseCaseWithUrl(alias = "docs-layouting")
//@SystemFunction(DocsSystemFunction.FH_DOCUMENTATION_VIEW)
public class LayoutingElementsUC implements IInitialUseCase {
    @Override
    public void start() {
        showForm(LayoutingElementsForm.class, null);
    }
}
