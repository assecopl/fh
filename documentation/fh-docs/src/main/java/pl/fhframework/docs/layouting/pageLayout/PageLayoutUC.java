package pl.fhframework.docs.layouting.pageLayout;

import pl.fhframework.annotations.Action;
import pl.fhframework.core.security.annotations.SystemFunction;
import pl.fhframework.core.uc.IInitialUseCase;
import pl.fhframework.core.uc.IUseCaseNoCallback;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.docs.DocsSystemFunction;

@UseCase
@UseCaseWithUrl(alias = "docs-layouting")
//@SystemFunction(DocsSystemFunction.FH_DOCUMENTATION_VIEW)
public class PageLayoutUC implements IInitialUseCase {
    @Override
    public void start() {
        showForm(PageLayoutForm.class, null);
    }


    @Action
    public void showFullWidthExample(){
        runUseCase(FullWidthPageUC.class, IUseCaseNoCallback.EMPTY);
    }

    @Action
    public void showGridLayoutExample(){ runUseCase(PanelsLayoutUC.class, IUseCaseNoCallback.EMPTY); }
}