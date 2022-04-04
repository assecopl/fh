package pl.fhframework.fhdp.example.fhml;

import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.annotations.Action;
import pl.fhframework.core.uc.IInitialUseCase;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.fhdp.example.i18n.ExampleAppMessageHelper;

@UseCase
@UseCaseWithUrl(alias = "sample-fhml-uc")
public class ExtendedFHMLViewUC implements IInitialUseCase {
    @Autowired
    private ExampleAppMessageHelper messageHelper;

    private ExtendedFHMLViewForm.Model model;

    @Override
    public void start() {
        ExtendedFHMLViewForm.Model model = new ExtendedFHMLViewForm.Model();
        model.setSampleField(messageHelper.getMessage("sample.test" ));

        this.model = model;

        showForm(ExtendedFHMLViewForm.class, this.model);
    }

    @Action
    public void close() {
        exit();
    }

}
