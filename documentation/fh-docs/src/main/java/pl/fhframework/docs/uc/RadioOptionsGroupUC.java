package pl.fhframework.docs.uc;

import pl.fhframework.core.designer.IDocumentationUseCase;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.docs.forms.component.RadioOptionsGroupForm;
import pl.fhframework.docs.forms.component.model.RadioOptionsGroupElement;
import pl.fhframework.annotations.Action;
import pl.fhframework.events.BreakLevelEnum;


/**
 * Use case supporting RadioOptionsGroup documentation
 */
@UseCase
public class RadioOptionsGroupUC implements IDocumentationUseCase<RadioOptionsGroupElement> {
    private RadioOptionsGroupElement model;

    @Override
    public void start(RadioOptionsGroupElement model) {
        this.model = model;
        showForm(RadioOptionsGroupForm.class, model);
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void polandClicked() {
        RadioOptionsGroupElement radioElement = model;
        radioElement.setSelectRadioGroupValue2("Poland");
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void germanClicked() {
        RadioOptionsGroupElement radioElement = model;
        radioElement.setSelectRadioGroupValue2("Germany");
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void ukClicked() {
        RadioOptionsGroupElement radioElement = model;
        radioElement.setSelectRadioGroupValue2("UK");
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void onChangeExample() {
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void usClicked() {
        RadioOptionsGroupElement radioElement = model;
        radioElement.setSelectRadioGroupValue2("USA");
    }

}

