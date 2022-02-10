package pl.fhframework.dp.commons.fh.parameters.list;

import pl.fhframework.SessionManager;
import pl.fhframework.annotations.Action;
import pl.fhframework.annotations.composite.Composite;
import pl.fhframework.core.uc.IUseCase;
import pl.fhframework.model.forms.CompositeForm;

@Composite(template = "SubstantiveParametersCTListSearch.frm")
public class SubstantiveParametersCTListSearch extends CompositeForm<SubstantiveParametersListModel> {
    @Override
    public void init() {
        super.init();
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

    @Action
    public void onClickGlobalSearchBoxButton(){
        IUseCase uc = SessionManager.getUserSession().getUseCaseContainer().getCurrentUseCaseContext().getUseCase();
        uc.runAction("onClickGlobalSearchBoxButton");
    }

    @Action
    public void onClickAllSearchBoxButton(){
        IUseCase uc = SessionManager.getUserSession().getUseCaseContainer().getCurrentUseCaseContext().getUseCase();
        uc.runAction("onClickAllSearchBoxButton");
    }

    @Action
    public void onClickOfficeSearchBoxButton(){
        IUseCase uc = SessionManager.getUserSession().getUseCaseContainer().getCurrentUseCaseContext().getUseCase();
        uc.runAction("onClickOfficeSearchBoxButton");
    }


}