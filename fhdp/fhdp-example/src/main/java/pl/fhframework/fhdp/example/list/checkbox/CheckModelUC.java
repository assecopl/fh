package pl.fhframework.fhdp.example.list.checkbox;

import pl.fhframework.annotations.Action;
import pl.fhframework.core.uc.IUseCaseNoCallback;
import pl.fhframework.core.uc.IUseCaseOneInput;
import pl.fhframework.core.uc.UseCase;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 09/08/2020
 */
@UseCase
public class CheckModelUC implements IUseCaseOneInput<CheckBoxOnTheListModel, IUseCaseNoCallback> {
    @Override
    public void start(CheckBoxOnTheListModel one) {
        showForm(getClass().getPackage().getName() + ".CheckModelForm", one);
    }

    @Action
    public void close() {
        exit();
    }
}
