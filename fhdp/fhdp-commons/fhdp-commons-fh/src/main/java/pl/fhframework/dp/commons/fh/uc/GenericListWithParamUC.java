package pl.fhframework.dp.commons.fh.uc;

import pl.fhframework.dp.commons.fh.model.IParamVariant;
import pl.fhframework.core.uc.IUseCaseOneInput;
import pl.fhframework.model.forms.Form;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 2019-08-15
 */
public abstract class GenericListWithParamUC<LIST, INPUT, PARAMS, RESULT> extends GenericBaseListUC<LIST, PARAMS, RESULT> implements IUseCaseOneInput<INPUT, IGenericListOutputCallback<INPUT>> {

    protected INPUT input;

    @Override
    public void start(INPUT input) {
        this.input = input;
        super.init();
    }

    @Override
    protected final LIST initInternalListData() {
        return initInternalListData(input);
    }

    protected abstract LIST initInternalListData(INPUT input);

    @Override
    protected Form showFormX(String listFormId, LIST listData) {
        String variant = null;
        if(input instanceof IParamVariant) {
            IParamVariant p = (IParamVariant) input;
            variant = p.getFormVariant();
        }
        if(variant == null) {
            return showForm(listFormId, listData);
        } else {
            return showForm(listFormId, listData, variant);
        }
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
