package pl.fhframework.docs.uc;

import pl.fhframework.core.designer.IDocumentationUseCase;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.docs.forms.component.IncludeForm;
import pl.fhframework.docs.forms.component.model.IncludeElement;
import pl.fhframework.annotations.Action;
import pl.fhframework.events.BreakLevelEnum;


/**
 * Use case supporting Include documentation
 */
@UseCase
public class IncludeUC implements IDocumentationUseCase<IncludeElement> {
    private IncludeElement model;

    @Override
    public void start(IncludeElement model) {
        this.model = model;
        showForm(IncludeForm.class, model);
    }


    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void onSave(){
        // address handler
		//Messages.showInfoMessage(getUserSession(), "Address saved");
    }


}

