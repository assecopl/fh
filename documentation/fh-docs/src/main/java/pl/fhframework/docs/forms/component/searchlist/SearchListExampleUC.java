package pl.fhframework.docs.forms.component.searchlist;

import pl.fhframework.core.security.annotations.SystemFunction;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.IInitialUseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.docs.DocsSystemFunction;
import pl.fhframework.annotations.Action;
import pl.fhframework.model.forms.model.OptionsListElementModel;
import pl.fhframework.events.ViewEvent;

/**
 * Created by krzysztof.kobylarek on 2017-01-14.
 */
@UseCase
@UseCaseWithUrl(alias = "docs-search-list")
//@SystemFunction(DocsSystemFunction.FH_DOCUMENTATION_VIEW)
public class SearchListExampleUC implements IInitialUseCase {

    private SearchListExampleModel model = new SearchListExampleModel();

    @Override
    public void start() {
        showForm(SearchListExampleForm.class, model);
    }

    @Action
    public void onSearch(ViewEvent<OptionsListElementModel> viewEvent) {
        // handler
        // FhLogger.info(this.getClass(), "viewEvent = [" + viewEvent + "]");
    }

    @Action
    public void onSend(ViewEvent<SearchListExampleModel> viewEvent) {
        showForm(SearchListSummary.class, model);
    }
}
