package pl.fhframework.dp.commons.fh.document.positionhandler;

import org.apache.commons.lang3.StringUtils;
import pl.fhframework.dp.commons.fh.document.handling.BaseDocumentFormModel;
import pl.fhframework.annotations.Action;
import pl.fhframework.core.uc.IUseCaseOneInput;
import pl.fhframework.core.uc.IUseCaseSaveCancelCallback;

/**
 * Base for specific usecases that are responsible for add, edit, delete of table elements on  generated forms
 * @param <MODEL>  is the model that holds table
 */

public abstract class TablePositionBaseUC<MODEL extends BaseDocumentFormModel> implements IUseCaseOneInput<MODEL, IUseCaseSaveCancelCallback<MODEL>> {
    private MODEL model;
    @Override
    public void start(MODEL one) {
        model = one;
        String positionEditFormReference = getPositionEditFormReference();
        if(StringUtils.isNotBlank(positionEditFormReference))
            showForm(positionEditFormReference, model, model.getVariant());
    }

    @Action
    public void save() {
        exit().save(model);
    }

    @Action(validate = false)
    public void cancel() {
        exit().cancel();
    }

    public abstract String getPositionEditFormReference();
}


