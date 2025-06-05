package pl.fhframework.docs.uc;

import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.annotations.Action;
import pl.fhframework.core.designer.IDocumentationUseCase;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.docs.forms.component.TableLazyForm;
import pl.fhframework.docs.forms.component.TablePagedForm;
import pl.fhframework.docs.forms.component.model.TableLazyElement;
import pl.fhframework.docs.forms.component.model.TablePagedElement;
import pl.fhframework.docs.forms.model.example.Person;
import pl.fhframework.docs.forms.service.CountryService;
import pl.fhframework.docs.forms.service.PersonService;
import pl.fhframework.events.BreakLevelEnum;
import pl.fhframework.model.forms.PageModel;

import java.util.List;

/**
 * Use case supporting table paged documentation
 */
@UseCase
public class TableLazyUC implements IDocumentationUseCase<TableLazyElement> {

    @Autowired
    private CountryService countryService;

    @Autowired
    private PersonService personService;

    private TableLazyElement model;

    private PageModel<CountryService.Country> originalPageModel;
    private PageModel<Person> pagedPeopleForRead;

    @Override
    public void start(TableLazyElement model) {
        this.model = model;
        // independent page models for each table
        model.setPagedPeople(new PageModel<>(personService::findAllPeople));
        model.setPagedPeopleForRead(new PageModel<>(personService::findAllPeople));
        model.setPageModelCountries(new PageModel<>(countryService::createPage));
        showForm(TableLazyForm.class, model);
    }
}

