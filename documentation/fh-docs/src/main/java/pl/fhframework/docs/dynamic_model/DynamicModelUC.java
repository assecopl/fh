package pl.fhframework.docs.dynamic_model;

import pl.fhframework.core.security.annotations.SystemFunction;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.IInitialUseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.docs.DocsSystemFunction;
import pl.fhframework.docs.dynamic_model.forms.DynamicModelForm;
import pl.fhframework.docs.dynamic_model.models.DynamicModelModel;

@UseCase
@UseCaseWithUrl(alias = "docs-dynamic-model")
//@SystemFunction(DocsSystemFunction.FH_DOCUMENTATION_VIEW)
public class DynamicModelUC implements IInitialUseCase {
    private DynamicModelForm form;

    private DynamicModelModel model = new DynamicModelModel();

    @Override
    public void start() {
        form = showForm(DynamicModelForm.class, model);
    }
}
