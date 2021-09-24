package pl.fhframework.core.externalusecase;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import pl.fhframework.ExternalUseCaseRegistry;
import pl.fhframework.annotations.Action;
import pl.fhframework.core.uc.IUseCaseOneInput;
import pl.fhframework.core.uc.IUseCaseOneOutputCallback;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.event.EventRegistry;

/**
 * FH use case which starts an external use case.
 * @author Tomasz.Kozlowski (created on 2017-11-02)
 */
@UseCase
@RequiredArgsConstructor
public class ExternalUseCase implements IUseCaseOneInput<ExternalUseCaseEntry, IUseCaseOneOutputCallback<Boolean>> {

    private final ExternalUseCaseRegistry externalRegistry;
    private final EventRegistry eventRegistry;

    private ExternalUseCaseForm form;
    @Setter
    private Thread authorizedThread;
    @Setter
    private boolean success;

    @Override
    public void start(ExternalUseCaseEntry entry) {
        validate(entry);
        externalRegistry.addUseCase(entry.getUuid(), this);
        form = showForm(ExternalUseCaseForm.class, null);
        eventRegistry.fireRedirectEvent(entry.getUuid(), entry.getExternalURL().toString(), true, entry.isCloseable());
    }

    /** Validate payment input parameter */
    private void validate(ExternalUseCaseEntry entry) {
        if (entry == null) {
            throw new IllegalArgumentException("ExternalUseCaseEntry cannot be null");
        }
        if (entry.getUuid() == null || entry.getUuid().isEmpty()) {
            throw new IllegalArgumentException("UUID cannot be null or empty");
        }
        if (entry.getExternalURL() == null) {
            throw new IllegalArgumentException("External URL cannot be null");
        }
    }

    @Action
    public void finish() {
        if (authorizedThread != Thread.currentThread()) {
            throw new IllegalStateException("Current thread is not authorized to perform this action");
        }
        hideForm(form);
        exit().output(success);
    }

}
