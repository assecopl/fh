package pl.fhframework.dp.commons.fh.adm.ui;

import lombok.RequiredArgsConstructor;
import pl.fhframework.dp.commons.fh.adm.security.FhAdmSystemFunction;
import pl.fhframework.dp.commons.fh.uc.FhdpBaseUC;
import pl.fhframework.annotations.Action;
import pl.fhframework.core.security.AuthorizationManager;
import pl.fhframework.core.security.annotations.SystemFunction;
import pl.fhframework.core.security.provider.service.SecurityDataProvider;
import pl.fhframework.core.uc.IUseCaseNoInput;
import pl.fhframework.core.uc.IUseCaseSaveCancelCallback;
import pl.fhframework.core.uc.UseCase;

import java.util.Set;

/**
 * @author Tomasz Kozlowski (created on 24.02.2021)
 */
@UseCase
@RequiredArgsConstructor
@SystemFunction(FhAdmSystemFunction.ADM_PERMISSIONS_EDIT)
public class FunctionsSelectUC extends FhdpBaseUC implements IUseCaseNoInput<IUseCaseSaveCancelCallback<Set<AuthorizationManager.Function>>> {

    private FunctionsSelectForm.Model model;

    private final SecurityDataProvider securityDataProvider;

    @Override
    public void start() {
        FunctionsTreeBuilder functionsTreeBuilder = new FunctionsTreeBuilder(
                securityDataProvider.getAllModules(),
                securityDataProvider.getAllSystemFunctions()
        );
        model = new FunctionsSelectForm.Model(functionsTreeBuilder);
        showForm(FunctionsSelectForm.class, model);
    }

    @Action
    public void confirm() {
        exit().save(model.getAddedFunctions());
    }

    @Action
    public void cancel() {
        exit().cancel();
    }

    @Action
    public void addSelectedFunctionsAsAllowed() {
        model.addSelectedFunctionsAsAllowed();
    }

    @Action
    public void addSelectedFunctionsAsDisallowed() {
        model.addSelectedFunctionsAsDisallowed();
    }

    @Action
    public void removeSelectedFunctions() {
        model.removeSelectedFunctions();
    }

}
