package pl.fhframework.docs.layouting.pageLayout;

import pl.fhframework.annotations.Action;
import pl.fhframework.core.security.annotations.SystemFunction;
import pl.fhframework.core.uc.IInitialUseCase;
import pl.fhframework.core.uc.IUseCaseNoCallback;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UseCaseWithLayout;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.docs.DocsSystemFunction;

@UseCase
@UseCaseWithUrl(alias = "docs-page-layouting-panels")
@UseCaseWithLayout(layout = "panels")
//@SystemFunction(DocsSystemFunction.FH_DOCUMENTATION_VIEW)
public class PanelsLayoutUC implements IInitialUseCase {
    @Override
    public void start() {
        showForm(PanelsLayoutSidePanelLeft.class, null);
        showForm(PanelsLayoutFormMain.class, null);
        showForm(PanelsLayoutSidePanelRight.class, null);
    }

    @Action
    public void backToLayouting(){
        runUseCase(PageLayoutUC.class, IUseCaseNoCallback.EMPTY);
    }
}
