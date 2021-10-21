package pl.fhframework.example;

import pl.fhframework.annotations.Action;
import pl.fhframework.core.uc.IInitialUseCase;
import pl.fhframework.core.uc.UseCase;


/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 23/09/2021
 */
@UseCase
public class FhDemoUC implements IInitialUseCase {
    @Override
    public void start() {
        showForm(FhDemoForm.class, "");
    }

    @Action
    public void onClose() {
        exit();
    }
}
