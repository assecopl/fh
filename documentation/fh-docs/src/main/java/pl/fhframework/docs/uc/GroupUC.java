package pl.fhframework.docs.uc;

import pl.fhframework.core.designer.IDocumentationUseCase;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.docs.forms.component.GroupForm;
import pl.fhframework.docs.forms.component.model.GroupElement;
import pl.fhframework.annotations.Action;
import pl.fhframework.events.BreakLevelEnum;
import pl.fhframework.model.forms.messages.Messages;


/**
 * Use case supporting Group documentation
 */
@UseCase
public class GroupUC implements IDocumentationUseCase<GroupElement> {
    //private GroupElement model;

    @Override
    public void start(GroupElement model) {
        //this.model = model;
        showForm(GroupForm.class, model);
    }


    // group action
    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void someActionGroup() {
        Messages.showInfoMessage(getUserSession(), "Group's action onClick - executed");
    }

    // group button action
    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void someActionButtonInGroup() {
        Messages.showInfoMessage(getUserSession(), "Button's action onClick - executed");
    }

    
}
