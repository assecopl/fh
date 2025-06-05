/**
 * *********************************************************
 * Author: krzysztof.kozlowski2
 * Created: 2018-03-16
 * **********************************************************
 */
package pl.fhframework.docs.creating_project.uc;

import pl.fhframework.core.security.annotations.SystemFunction;
import pl.fhframework.core.uc.IInitialUseCase;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.docs.DocsSystemFunction;
import pl.fhframework.docs.creating_project.form.CreatingNewModuleForm;

@UseCase
@UseCaseWithUrl(alias = "docs-creating-new-module")
//@SystemFunction(DocsSystemFunction.FH_DOCUMENTATION_VIEW)
public class CreatingNewModuleUC implements IInitialUseCase {
    @Override
    public void start() {
        showForm(CreatingNewModuleForm.class, null);
    }
}
