package pl.fhframework.dp.commons.fh.declaration.positionhandler;

import org.apache.commons.lang3.StringUtils;
import pl.fhframework.dp.commons.fh.declaration.handling.BaseDeclarationFormModel;
import pl.fhframework.annotations.Action;
import pl.fhframework.core.uc.IUseCaseOneInput;
import pl.fhframework.core.uc.IUseCaseSaveCancelCallback;

/**
 * Base for specific usecases that are responsible for add, edit, delete of table elements on  generated forms
 * @param <MODEL>  is the model that holds table
 */

public abstract class TablePositionBaseUC<MODEL extends BaseDeclarationFormModel> implements IUseCaseOneInput<MODEL, IUseCaseSaveCancelCallback<MODEL>> {
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


