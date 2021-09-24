package pl.fhframework.core.uc.handlers;

import pl.fhframework.core.uc.FormsContainer;
import pl.fhframework.core.uc.UseCaseContainer;

/**
 * Created by pawel.ruta on 2018-11-23.
 */
public interface INoFormHandler {
    String NO_FORM_MESSAGE = "fh.core.no.form";

    void handleNoFormCase(UseCaseContainer useCaseContainer, UseCaseContainer.UseCaseContext eventUseCaseContext, FormsContainer formsContainer);

    default String buildMessage() {
        return null;
    }

    default String messageTitle() {
        return null;
    }
}
