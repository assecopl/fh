package pl.fhframework.docs.uc;

import pl.fhframework.core.designer.IDocumentationUseCase;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.docs.forms.component.ThreeDotsMenuForm;
import pl.fhframework.docs.forms.component.model.ThreeDotsMenuElement;

@UseCase
public class ThreeDotsMenuUC implements IDocumentationUseCase<ThreeDotsMenuElement> {
    private ThreeDotsMenuElement model;

    @Override
    public void start(ThreeDotsMenuElement model) {
        this.model = model;
        showForm(ThreeDotsMenuForm.class, model);
    }
}

