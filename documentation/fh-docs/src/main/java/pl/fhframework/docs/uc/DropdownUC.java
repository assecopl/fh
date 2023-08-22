package pl.fhframework.docs.uc;

import pl.fhframework.core.designer.IDocumentationUseCase;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.docs.forms.component.DropdownForm;
import pl.fhframework.docs.forms.component.model.DropdownElement;
import pl.fhframework.annotations.Action;
import pl.fhframework.events.BreakLevelEnum;


/**
 * Use case supporting Dropdown documentation
 */
@UseCase
public class DropdownUC implements IDocumentationUseCase<DropdownElement> {
    private DropdownElement model;

    @Override
    public void start(DropdownElement model) {
        this.model = model;
        showForm(DropdownForm.class, model);
    }


    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void onDropdownItemClick() {
        boolean initialValue = model.isInitialValue();
        model.setInitialValue(!initialValue);
        if (initialValue) {
            model.setDropdownLabel(model.getInitialLabel());
        } else {
            model.setDropdownLabel(model.getChangedLabel());
        }
        FhLogger.debug(this.getClass(), logger -> logger.log("DropdownItem clicked"));
    }


}

