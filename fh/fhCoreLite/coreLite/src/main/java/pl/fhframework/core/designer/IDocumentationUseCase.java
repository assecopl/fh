package pl.fhframework.core.designer;

import pl.fhframework.core.uc.IUseCaseNoCallback;
import pl.fhframework.core.uc.IUseCaseOneInput;
import pl.fhframework.annotations.Action;
import pl.fhframework.events.BreakLevelEnum;

/**
 * Interface of use case used in component documentation.
 */
public interface IDocumentationUseCase<T extends ComponentElement> extends IUseCaseOneInput<T, IUseCaseNoCallback> {

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public default void backToFormComponentsList() {
        exit();
    }
}
