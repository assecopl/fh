package pl.fhframework.dp.commons.fh.messages;

import pl.fhframework.SessionManager;
import pl.fhframework.annotations.Action;
import pl.fhframework.annotations.composite.Composite;
import pl.fhframework.core.uc.IUseCase;
import pl.fhframework.model.forms.CompositeForm;

@Composite(template = "MessageSearchCTListSearch.frm")
public class MessageSearchCTListSearch extends CompositeForm<MessageSearchListModel> {
    @Override
    public void init() {
        super.init();
    }

    @Action
    public void openSearchByDetails(){
        IUseCase uc = SessionManager.getUserSession().getUseCaseContainer().getCurrentUseCaseContext().getUseCase();
        uc.runAction("openSearchByDetails");
    }

    @Action
    public void search() {
        IUseCase uc = SessionManager.getUserSession().getUseCaseContainer().getCurrentUseCaseContext().getUseCase();
        uc.runAction("search");
    }

    @Action
    public void clearQuery() {
        IUseCase uc = SessionManager.getUserSession().getUseCaseContainer().getCurrentUseCaseContext().getUseCase();
        uc.runAction("clearQuery");
    }
}