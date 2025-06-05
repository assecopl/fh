package pl.fhframework.docs.exception.uc;

import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.core.security.annotations.SystemFunction;
import pl.fhframework.core.uc.IInitialUseCase;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.docs.DocsSystemFunction;
import pl.fhframework.docs.exception.form.FhDocumentedExceptionForm;
import pl.fhframework.docs.exception.form.FhDocumentedExceptionModel;
import pl.fhframework.docs.exception.service.FhDocumentedExceptionService;

/**
 * Created by k.czajkowski on 28.02.2017.
 */
@UseCase
@UseCaseWithUrl(alias = "docs-exceptions")
//@SystemFunction(DocsSystemFunction.FH_DOCUMENTATION_VIEW)
public class FhDocumentedExceptionUC implements IInitialUseCase {

    private FhDocumentedExceptionModel model;

    @Autowired
    private FhDocumentedExceptionService service;

    @Override
    public void start() {
        model = new FhDocumentedExceptionModel(service);
        showForm(FhDocumentedExceptionForm.class, model);
    }
}