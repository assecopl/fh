package pl.fhframework.docs.uc;

import pl.fhframework.annotations.Action;
import pl.fhframework.core.designer.IDocumentationUseCase;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.docs.forms.component.AnchorForm;
import pl.fhframework.docs.forms.component.PanelGroupWrapperForm;
import pl.fhframework.docs.forms.component.model.AnchorElement;
import pl.fhframework.docs.forms.component.model.PanelGroupWrapperElement;


/**
 * Use case supporting Button documentation
 */
@UseCase
public class PanelGroupWrapperUC implements IDocumentationUseCase<PanelGroupWrapperElement> {
    private PanelGroupWrapperElement model;

    @Override
    public void start(PanelGroupWrapperElement model) {
        this.model = model;
        showForm(PanelGroupWrapperForm.class, model);
    }

    @Action
    public void toggle(){
        this.model.setToggleAll(!this.model.toggleAll);
    }




}

