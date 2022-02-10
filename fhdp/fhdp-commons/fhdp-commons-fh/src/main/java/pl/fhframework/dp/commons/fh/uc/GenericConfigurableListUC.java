package pl.fhframework.dp.commons.fh.uc;

import pl.fhframework.core.uc.IUseCaseNoCallback;
import pl.fhframework.core.uc.IUseCaseOneInput;
import pl.fhframework.model.forms.Form;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 2019-08-15
 */
public abstract class GenericConfigurableListUC<CONFIG, LIST, PARAMS, RESULT> extends GenericBaseListUC<LIST, PARAMS, RESULT> implements IUseCaseOneInput<CONFIG, IUseCaseNoCallback> {
    CONFIG config;
    @Override
    public void start(CONFIG config) {
        configureBeforeInit(config);
        listData = initInternalListData(config);
        String listFormId = getListFormId();
        if(isImmediate()) {
            readData();
        }
        form = showFormX(listFormId, listData);
    }

    /**
     * Returns initiated list model class.
     *
     * @return
     */
    protected abstract LIST initInternalListData(CONFIG config);

    @Override
    protected LIST initInternalListData(){ throw new  UnsupportedOperationException(); }

    /**
     * performs configuration prior to init
     * @param config
     */
    public abstract void configureBeforeInit(CONFIG config);


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
