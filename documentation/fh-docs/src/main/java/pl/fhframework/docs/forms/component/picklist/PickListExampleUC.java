package pl.fhframework.docs.forms.component.picklist;

import pl.fhframework.core.security.annotations.SystemFunction;
import pl.fhframework.core.uc.IInitialUseCase;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.docs.DocsSystemFunction;
import pl.fhframework.annotations.Action;
import pl.fhframework.model.forms.composites.picklist.PickListModel;
import pl.fhframework.model.forms.model.OptionsListElementModel;
import pl.fhframework.events.ViewEvent;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by krzysztof.kobylarek on 2017-01-09.
 */
@UseCase
@UseCaseWithUrl(alias = "docs-pick-list")
//@SystemFunction(DocsSystemFunction.FH_DOCUMENTATION_VIEW)
public class PickListExampleUC implements IInitialUseCase {

    private PickListExampleForm pickListForm = null;
    private PickListExampleFormModel model = new PickListExampleFormModel();

    public static class PickListModelWrapper {

        private PickListModel model;

        public PickListModelWrapper(PickListModel model) {
            this.model = model;
        }

        public List<OptionsListElementModel> getElementsAvailable() {
            return model.getValuesList1();
        }

        public List<OptionsListElementModel> getElementsOrdered() {
            return model.getValuesList2();
        }

        public List<OptionsListElementModel> getElementsAvailableChecked() {
            return model.getValuesList1().stream().filter((e) -> e.isChecked()).collect(Collectors.toList());
        }

        public List<OptionsListElementModel> getElementsOrderedChecked() {
            return model.getValuesList2().stream().filter((e) -> e.isChecked()).collect(Collectors.toList());
        }
    }

    @Override
    public void start() {
        pickListForm = showForm(PickListExampleForm.class, model);
    }

    @Action
    public void onList1Changed(ViewEvent<PickListModel> viewEvent){
        //FhLogger.info(this.getClass(), "PickListExampleUC.onList1Changed");
    }

    @Action
    public void onList2Changed(ViewEvent<PickListModel> viewEvent){
        //FhLogger.info(this.getClass(), "PickListExampleUC.onList2Changed");
    }

    @Action
    public void saveCarsList(ViewEvent<PickListExampleFormModel> event){
        PickListModel model = event.getSourceForm().getModel().getCarsListModel();
        showForm(PickListSummary.class, new PickListModelWrapper(model));
    }
}
