package pl.fhframework;

import pl.fhframework.core.uc.IUseCase;
import pl.fhframework.core.uc.IUseCaseOneInput;
import pl.fhframework.core.uc.IUseCaseOneOutputCallback;
import pl.fhframework.core.uc.IUseCaseTwoOutputCallback;


/**
 * Created by Gabriel on 12.01.2016.
 */
public abstract class UseCaseWithOneOutput<INPUT, OUTPUT> extends UseCaseAdapter<INPUT, IUseCaseOneOutputCallback<OUTPUT>> implements IUseCaseOneInput<INPUT, IUseCaseOneOutputCallback<OUTPUT>> {
    protected void useCaseReturn(OUTPUT output) {
        exit().output(output);
    }
}
