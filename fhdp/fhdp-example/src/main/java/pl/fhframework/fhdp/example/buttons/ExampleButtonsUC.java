package pl.fhframework.fhdp.example.buttons;

import pl.fhframework.annotations.Action;
import pl.fhframework.core.uc.IInitialUseCase;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.model.forms.messages.Messages;

@UseCase
public class ExampleButtonsUC implements IInitialUseCase {

    private ExampleButtonsForm.Model model;

    @Override
    public void start() {
        ExampleButtonsForm.Model model = new ExampleButtonsForm.Model();
        this.model = model;
        showForm(ExampleButtonsForm.class, this.model);
    }

    @Action
    public void close() {
        exit();
    }

    @Action
    public void test() {
        Messages.showInfoMessage(getUserSession(), "test");
    }

    @Action
    public void testError() {
        throw new RuntimeException("test exception");
    }

    @Action
    public void action(String action, boolean validate) {
        Messages.showInfoMessage(getUserSession(), "Action: " + action);
    }

    @Action
    public void handleInterval() {
        if (this.model.getIntervalText() > 9) {
            this.model.setIntervalText(0);
        } else {
            this.model.setIntervalText(this.model.getIntervalText() + 1);
        }
    }

}
