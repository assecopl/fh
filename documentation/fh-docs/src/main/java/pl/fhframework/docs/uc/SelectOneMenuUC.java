package pl.fhframework.docs.uc;

import pl.fhframework.core.designer.IDocumentationUseCase;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.docs.converter.model.User;
import pl.fhframework.docs.forms.component.SelectOneMenuForm;
import pl.fhframework.docs.forms.component.model.SelectOneMenuElement;
import pl.fhframework.docs.forms.model.example.SizeEnum;
import pl.fhframework.annotations.Action;
import pl.fhframework.events.BreakLevelEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Use case supporting SelectOneMenu documentation
 */
@UseCase
public class SelectOneMenuUC implements IDocumentationUseCase<SelectOneMenuElement> {
    private SelectOneMenuElement model;

    @Override
    public void start(SelectOneMenuElement model) {
        this.model = model;
        showForm(SelectOneMenuForm.class, model);
    }


    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void removeValue(SizeEnum selectedSize) {
        List<SizeEnum> sizes = new ArrayList<>(model.getSizes());
        sizes.remove(selectedSize);
        model.setSizes(sizes);
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void addSampleUser(){
        List<User> boundUsers = model.getBoundUsers();
        Random r = new Random();
        User user = boundUsers.get(r.ints(0, boundUsers.size() - 1).findFirst().getAsInt());
        boundUsers.add(user);
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void onChangeExample() {
        FhLogger.debug(this.getClass(), logger -> logger.log("Hello onChange event example."));
    }


}

