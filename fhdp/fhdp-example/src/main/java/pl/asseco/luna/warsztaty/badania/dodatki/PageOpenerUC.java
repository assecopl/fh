package pl.asseco.luna.warsztaty.badania.dodatki;

import pl.fhframework.annotations.Action;
import pl.fhframework.core.uc.IInitialUseCase;
import pl.fhframework.core.uc.IUseCaseNoCallback;
import pl.fhframework.core.uc.IUseCaseOneInput;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.event.EventRegistry;
import pl.fhframework.fhdp.example.buttons.ExampleButtonsForm;
import pl.fhframework.model.forms.messages.Messages;
import org.springframework.beans.factory.annotation.Autowired;

@UseCase
public class PageOpenerUC implements IUseCaseOneInput<String, IUseCaseNoCallback> {
    @Autowired
    private EventRegistry eventRegistry;

    @Override
    public void start(String url) {
        eventRegistry.fireRedirectEvent(url, true);
    }
}
