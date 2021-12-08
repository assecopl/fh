package pl.fhframework.dp.commons.fh.messages;

import pl.fhframework.SessionManager;
import pl.fhframework.annotations.Action;
import pl.fhframework.annotations.composite.Composite;
import pl.fhframework.core.uc.IUseCase;
import pl.fhframework.model.forms.CompositeForm;

@Composite(template = "MessageSearchByDetailsForm.frm")
public class MessageSearchByDetailsForm extends CompositeForm<MessageSearchListModel> {
    @Override
    public void init() {
        super.init();
    }

    @Action
    public void closeSearchByDetails() {
        IUseCase uc = SessionManager.getUserSession().getUseCaseContainer().getCurrentUseCaseContext().getUseCase();
        uc.runAction("closeSearchByDetails");
    }
}
