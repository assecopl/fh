package pl.fhframework.docs.uc;

import lombok.Getter;
import pl.fhframework.core.designer.IDocumentationUseCase;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.docs.forms.component.OptionsListForm;
import pl.fhframework.docs.forms.component.OptionsListSummaryForm;
import pl.fhframework.docs.forms.component.model.OptionsListElement;
import pl.fhframework.annotations.Action;
import pl.fhframework.events.BreakLevelEnum;
import pl.fhframework.model.forms.model.OptionsListElementModel;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Use case supporting OptionsList documentation
 */
@UseCase
public class OptionsListUC implements IDocumentationUseCase<OptionsListElement> {
    private OptionsListElement model;

    public static class CheckedElements {

        @Getter
        private List<OptionsListElementModel> checkedElements;

        public CheckedElements(OptionsListElement model) {
            checkedElements =  model.getElements().stream().filter(OptionsListElementModel::isChecked).collect(Collectors.toList());
        }
    }


    @Override
    public void start(OptionsListElement model) {
        this.model = model;
        showForm(OptionsListForm.class, model);
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void onAddElementMiddle(){
        model.getElements().add(model.getElements().size()/2, new OptionsListElementModel(model.getCounter().get(),
                "Element "+model.getCounter().getAndIncrement()));
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void onAddElementFirst(){
        model.getElements().addFirst(new OptionsListElementModel(model.getCounter().get(),
                "Element " + model.getCounter().getAndIncrement()));
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void onAddElementLast(){
        model.getElements().addLast(new OptionsListElementModel(model.getCounter().get(),
                "Element " + model.getCounter().getAndIncrement()));
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void onAddEmptyElement(){
        model.getElements().addLast(new OptionsListElementModel(model.getCounter().getAndIncrement(), ""));
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void onRemoveFirst(){
        if (model.getElements().size()>0) {
            model.getElements().remove();
            model.getCounter().getAndDecrement();
        }
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void onRemoveLast(){
        if (model.getElements().size()>0) {
            model.getElements().removeLast();
            model.getCounter().getAndDecrement();
        }
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void onBtnListClicked(){
        showForm(OptionsListSummaryForm.class, new CheckedElements(model));
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void onSelectedListWithEmptyElement(){
        //Class<?> aClass = model.getClass();
        List<OptionsListElementModel> listWithEmptyElement = model.getListWithEmptyElement();
        List<OptionsListElementModel> selectedList = new LinkedList<>();
        OptionsListElementModel selectedEmptyElement = null;
        boolean emptySelected = false;
        for (OptionsListElementModel el : listWithEmptyElement) {
            if (el.isChecked()) {
                selectedList.add(el);

                if (el.getId() < 0) {
                    emptySelected = true;
                    selectedEmptyElement = el;
                }
            }
        }
        model.setSelectedList(selectedList);
        if (emptySelected) {
            model.setEmptyListElement(selectedEmptyElement);
        } else {
            model.setEmptyListElement(null);
        }
    }

}

