package pl.fhframework.fhdp.example.hint;

import pl.fhframework.core.uc.IInitialUseCase;
import pl.fhframework.core.uc.UseCase;

@UseCase
public class ExampleHintUC implements IInitialUseCase {

    private ExampleHintForm.Model model;

    @Override
    public void start() {
        ExampleHintForm.Model model = new ExampleHintForm.Model();
        this.model = model;

        showForm(ExampleHintForm.class, this.model);
    }

}
