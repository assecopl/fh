package pl.fhframework.fhdp.example.table;

import pl.fhframework.annotations.Action;
import pl.fhframework.core.uc.IUseCaseOneInput;
import pl.fhframework.core.uc.IUseCaseSaveCancelCallback;
import pl.fhframework.core.uc.UseCase;

@UseCase
public class ExampleTableAttributeUC implements IUseCaseOneInput<ExampleTableModel, IUseCaseSaveCancelCallback<ExampleTableModel>> {
    private ExampleTableModel model;

    @Override
    public void start(ExampleTableModel model) {
        this.model = model;
        showForm(getClass().getPackage().getName() + ".ExampleTableAttribute", model);
    }

    @Action
    public void close() {
        exit().cancel();
    }

    @Action
    public void save() {
        exit().save(model);
    }
}
