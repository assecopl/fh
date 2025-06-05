package pl.fhframework.docs.application.management;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import pl.fhframework.core.security.annotations.SystemFunction;
import pl.fhframework.core.uc.IInitialUseCase;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.docs.DocsSystemFunction;

/**
 * Created by Adam Zareba on 02.02.2017.
 */
@UseCase
@UseCaseWithUrl(alias = "docs-app-modes")
//@SystemFunction(DocsSystemFunction.FH_DOCUMENTATION_VIEW)
public class ApplicationModesUC implements IInitialUseCase {

    private ApplicationModesModel model = new ApplicationModesModel();

    @Override
    public void start() {
        showForm(ApplicationModesForm.class, model);
    }
}
