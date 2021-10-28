package pl.fhframework.example;

import pl.fhframework.annotations.Action;
import pl.fhframework.core.uc.IInitialUseCase;
import pl.fhframework.core.uc.UseCase;


@UseCase
public class FhDemoUC implements IInitialUseCase {
    @Override
    public void start() {
        showForm(FhDemoForm.class, "");
    }

    @Action
    public void onClose() {
        exit();
    }
}
