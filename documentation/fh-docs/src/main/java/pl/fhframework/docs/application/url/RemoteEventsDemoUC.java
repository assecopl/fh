package pl.fhframework.docs.application.url;

import pl.fhframework.annotations.Action;
import pl.fhframework.core.uc.*;
import pl.fhframework.model.forms.messages.Messages;

import java.util.Map;

@UseCase
public class RemoteEventsDemoUC implements IUseCaseNoInput<IUseCaseNoCallback>, ICommunicationUseCase {

    @Override
    public void start() {
        showForm(RemoteEventsDemoForm.class, new RemoteEventsDemoForm.Model());
    }

    @Action(immediate = true)
    void onExit() {
        exit();
    }

    @Action(value = "age", remote = true, defaultRemote = true)
    void onAgeEvent(@Parameter(name = "age", comment = "What's your age?") Integer age) {
        Messages.showInfoMessage(getUserSession(), String.format("'age' event called with Integer value = %s", age));
    }

    @Action(value = "name", remote = true)
    void onNameEvent(@Parameter(name = "name", comment = "What's your name?") String name) {
        Messages.showInfoMessage(getUserSession(), String.format("'name' event called with String value = %s", name));
    }

    @Action(value = "any", remote = true)
    void onNameEvent(@Parameter(name = "parameters", comment = "any parameters as MapOfStrings") Map<String, String> params) {
        Messages.showInfoMessage(getUserSession(), String.format("'any' event called with params %s", params.toString()));
    }
}
