package pl.fhframework.docs.uc;

import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.core.designer.IDocumentationUseCase;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.docs.forms.component.TableOptimizedForm;
import pl.fhframework.docs.forms.component.model.TableOptimizedElement;
import pl.fhframework.docs.forms.model.example.Person;
import pl.fhframework.docs.forms.service.CountryService;
import pl.fhframework.docs.forms.service.PersonService;
import pl.fhframework.model.forms.PageModel;

/**
 * Use case supporting table paged documentation
 */
@UseCase
public class TableOptimizedUC implements IDocumentationUseCase<TableOptimizedElement> {

    @Autowired
    private CountryService countryService;

    @Autowired
    private PersonService personService;

    private TableOptimizedElement model;

    private PageModel<CountryService.Country> originalPageModel;
    private PageModel<Person> pagedPeopleForRead;

    @Override
    public void start(TableOptimizedElement model) {
        this.model = model;
        // independent page models for each table
        model.setModelCountries(countryService.findAll());
        showForm(TableOptimizedForm.class, model);
    }
}

