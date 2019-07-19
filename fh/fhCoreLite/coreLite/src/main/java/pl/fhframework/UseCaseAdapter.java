package pl.fhframework;

import pl.fhframework.core.FhFormException;
import pl.fhframework.core.uc.*;
import pl.fhframework.model.PresentationStyleEnum;
import pl.fhframework.model.forms.Form;

import java.util.function.Consumer;

/**
 * Created by pawel.ruta on 17.02.2017.
 * <p>
 * Deprecated - należy używać intefejsów IUseCaseOneInput, IUseCaseNoInput, IUseCaseTwoInput
 */
@Deprecated
public abstract class UseCaseAdapter<INPUT, C extends IUseCaseOutputCallback> implements IUseCase<C> {
    public <T, F extends Form<T>> F wyswietlFormatke(String identyfikatorFormatki, T model) {
        return showForm((Class<F>) dajKlase(identyfikatorFormatki), model);
    }

    public <T, F extends Form<T>> F wyswietlFormatke(String identyfikatorFormatki, T model, String variantId) {
        return showForm((Class<F>) dajKlase(identyfikatorFormatki), model, variantId);
    }

    public <T, F extends Form<T>> F wyswietlFormatke(Class<F> formClazz, T model) {
        return showForm(formClazz, model);
    }

    public <T, F extends Form<T>> F wyswietlFormatke(Class<F> formClazz, T model, String formVariant) {
        return showForm(formClazz, model, formVariant);
    }

    public void zamknijFormatke(Form form) {
        hideForm(form);
    }

    public void wyswietlFormatke(Form form) {
        showForm(form);
    }

    public UserSession getSesjaUzytkownika() {
        return getUserSession();
    }

    public void zglosBladWalidacji(Object parent, String attributeName, String message, PresentationStyleEnum errorStyle) {
        reportValidationError(parent, attributeName, message, errorStyle);
    }

    public <INPUT, OUTPUT1, OUTPUT2, T extends IUseCaseOneInput<INPUT, IUseCaseTwoOutputCallback<OUTPUT1, OUTPUT2>>> void uruchomPodprzypadek(Class<T> klasaPodprzypadkuUzycia, INPUT daneWejsciowe, Consumer<OUTPUT1> metodaDlaWyjscia1, Consumer<OUTPUT2> metodaDlaWyjscia2) {
        runSubUseCase(klasaPodprzypadkuUzycia, daneWejsciowe, IUseCaseTwoOutputCallback.getCallback(metodaDlaWyjscia1, metodaDlaWyjscia2));
    }

    public <INPUT, OUTPUT, T extends IUseCaseOneInput<INPUT, IUseCaseOneOutputCallback<OUTPUT>>> void uruchomPodprzypadek(Class<T> klasaPodprzypadkuUzycia, INPUT daneWejsciowe, Consumer<OUTPUT> metodaWyjsciowa) {
        runSubUseCase(klasaPodprzypadkuUzycia, daneWejsciowe, IUseCaseOneOutputCallback.getCallback(metodaWyjsciowa));
    }

    public <INPUT, OUTPUT1, OUTPUT2, T extends IUseCaseOneInput<INPUT, IUseCaseTwoOutputCallback<OUTPUT1, OUTPUT2>>> void uruchomPrzypadek(Class<T> klasaPodprzypadkuUzycia, INPUT daneWejsciowe, Consumer<OUTPUT1> metodaDlaWyjscia1, Consumer<OUTPUT2> metodaDlaWyjscia2) {
        runUseCase(klasaPodprzypadkuUzycia, daneWejsciowe, IUseCaseTwoOutputCallback.getCallback(metodaDlaWyjscia1, metodaDlaWyjscia2));
    }

    public <INPUT, OUTPUT, T extends IUseCaseOneInput<INPUT, IUseCaseOneOutputCallback<OUTPUT>>> void uruchomPrzypadek(Class<T> klasaPodprzypadkuUzycia, INPUT daneWejsciowe, Consumer<OUTPUT> metodaWyjsciowa) {
        runUseCase(klasaPodprzypadkuUzycia, daneWejsciowe, IUseCaseOneOutputCallback.getCallback(metodaWyjsciowa));
    }

    private Class dajKlase(String identyfikatorFormatki) {
        try {
            return Class.forName(identyfikatorFormatki);
        } catch (ClassNotFoundException e) {
            throw new FhFormException("No form with class: ".concat(identyfikatorFormatki));
        }
    }
}
