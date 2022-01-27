package pl.fhframework.dp.commons.fh.uc;

import pl.fhframework.core.uc.IInitialUseCase;
import pl.fhframework.model.forms.Form;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 2019-08-15
 */
public abstract class GenericListUC<LIST, PARAMS, RESULT> extends GenericBaseListUC<LIST, PARAMS, RESULT> implements IInitialUseCase {

    @Override
    public void start() {
        super.init();
    }

    @Override
    protected Form showFormX(String listFormId, LIST listData) {
        return showForm(listFormId, listData);
    }

    @Override
    protected void exitX() {
        exit();
    }

    @Override
    protected void runUseCaseX(Class editFormClass, PARAMS params, IGenericListOutputCallback editListOutputCallback) {
        runUseCase(editFormClass, params, editListOutputCallback);
    }
}
