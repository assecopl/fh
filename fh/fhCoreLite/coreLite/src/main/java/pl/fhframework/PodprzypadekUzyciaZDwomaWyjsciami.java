package pl.fhframework;

import pl.fhframework.core.uc.*;

/**
 * Created by Gabriel on 17.01.2016.
 *
 * Deprecated - należy używać intefejsów IUseCaseOneInput, IUseCaseNoInput, IUseCaseTwoInput
 */
@Deprecated
public abstract class PodprzypadekUzyciaZDwomaWyjsciami<INPUT, OUTPUT1, OUTPUT2> extends UseCaseAdapter<INPUT, IUseCaseTwoOutputCallback<OUTPUT1, OUTPUT2>> implements IUseCaseOneInput<INPUT, IUseCaseTwoOutputCallback<OUTPUT1, OUTPUT2>> {

    protected void powrot1(OUTPUT1 output1) {
        exit().output1(output1);
    }

    protected void powrot2(OUTPUT2 output2) {
        exit().output2(output2);
    }
}

