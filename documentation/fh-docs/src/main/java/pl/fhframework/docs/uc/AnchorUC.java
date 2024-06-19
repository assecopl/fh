package pl.fhframework.docs.uc;

import pl.fhframework.annotations.Action;
import pl.fhframework.core.designer.IDocumentationUseCase;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.docs.forms.component.AnchorForm;
import pl.fhframework.docs.forms.component.model.AnchorElement;


/**
 * Use case supporting Button documentation
 */
@UseCase
public class AnchorUC implements IDocumentationUseCase<AnchorElement> {
    private AnchorElement model;

    @Override
    public void start(AnchorElement model) {
        this.model = model;
        this.model.setDuration(1000);
        this.model.setScrollRightNow(false);
        showForm(AnchorForm.class, model);
    }

    @Action
    public void scrollDown(){
        this.model.setScrollRightNow(true);
    }

    @Action
    public void scrollDownInside(){
        this.model.setScrollRightNowInside(true);
    }
    @Action
    public void scrollDownInside2(){
        this.model.setScrollRightNowInside2(true);
    }
    @Action
    public void scrollDownInside3(){
        this.model.setScrollRightNowInside3(true);
    }

    @Action
    public void changeDuration(){
        this.model.setDuration(3000);
    }


}

