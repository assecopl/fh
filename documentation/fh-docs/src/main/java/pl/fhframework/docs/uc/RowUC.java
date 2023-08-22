package pl.fhframework.docs.uc;

import pl.fhframework.core.designer.IDocumentationUseCase;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.docs.forms.component.RowForm;
import pl.fhframework.docs.forms.component.model.RowElement;

/**
 * Use case supporting Row documentation
 */
@UseCase
public class RowUC implements IDocumentationUseCase<RowElement> {
    private RowElement model;

    @Override
    public void start(RowElement model) {
        this.model = model;
        showForm(RowForm.class, model);
    }
}

