package pl.fhframework.fhdp.example.lookup;

import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.annotations.Action;
import pl.fhframework.core.uc.IInitialUseCase;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.fhdp.example.i18n.ExampleAppMessageHelper;
import pl.fhframework.fhdp.example.lookup.rest.CountryApiResponse;
import pl.fhframework.fhdp.example.lookup.rest.RestClient;

@UseCase
@UseCaseWithUrl(alias = "lookup-uc")
public class LookupUC implements IInitialUseCase {
    @Autowired
    private ExampleAppMessageHelper messageHelper;
    @Autowired
    RestClient restClient;

    private LookupForm.Model model;

    @Override
    public void start() {
        LookupForm.Model model = new LookupForm.Model();


        this.model = model;

        showForm(LookupForm.class, this.model);
    }

    @Action
    public void testRest() {
        CountryApiResponse result = restClient.listCountries(model.getCountryCode());
    }

    @Action
    public void close() {
        exit();
    }

    @Action(validate = false)
    public void setCountry(String countryCode){
        model.setCountryCode(countryCode);
    }

}
