package pl.fhframework.docs.uc;

import pl.fhframework.annotations.Action;
import pl.fhframework.core.designer.IDocumentationUseCase;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.docs.converter.model.User;
import pl.fhframework.docs.forms.component.SelectComboMenuForm;
import pl.fhframework.docs.forms.component.SelectOneMenuForm;
import pl.fhframework.docs.forms.component.model.SelectComboMenuElement;
import pl.fhframework.docs.forms.component.model.SelectOneMenuElement;
import pl.fhframework.docs.forms.model.example.SizeEnum;
import pl.fhframework.events.BreakLevelEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Use case supporting SelectOneMenu documentation
 */
@UseCase
public class SelectComboMenuUC implements IDocumentationUseCase<SelectComboMenuElement> {
    private SelectComboMenuElement model;

    @Override
    public void start(SelectComboMenuElement model) {
        this.model = model;
        showForm(SelectComboMenuForm.class, model);
    }


    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void removeValue() {
        model.setSimpleString(null);
        model.setSimpleSelectedComboMenu(null);
    }




}

