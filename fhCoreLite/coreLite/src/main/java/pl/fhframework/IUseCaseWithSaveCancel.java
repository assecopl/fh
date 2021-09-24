package pl.fhframework;

import pl.fhframework.core.uc.IUseCase;
import pl.fhframework.core.uc.IUseCaseOneInput;
import pl.fhframework.core.uc.IUseCaseSaveCancelCallback;
import pl.fhframework.core.uc.IUseCaseTwoOutputCallback;

import java.util.function.Consumer;

/**
 * Created by Paweł Ruta on 15.02.2017.
 */
public interface IUseCaseWithSaveCancel<INPUT, OUTPUT> extends IUseCaseOneInput<INPUT, IUseCaseSaveCancelCallback<OUTPUT>> {
}
