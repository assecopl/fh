package pl.fhframework.docs.uc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import pl.fhframework.core.designer.IDocumentationUseCase;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.docs.forms.component.TablePagedForm;
import pl.fhframework.docs.forms.component.model.TablePagedElement;
import pl.fhframework.docs.forms.model.example.Person;
import pl.fhframework.docs.forms.service.CountryService;
import pl.fhframework.docs.forms.service.PersonService;
import pl.fhframework.annotations.Action;
import pl.fhframework.events.BreakLevelEnum;
import pl.fhframework.events.ViewEvent;
import pl.fhframework.model.forms.Component;
import pl.fhframework.model.forms.PageModel;
import pl.fhframework.model.forms.TablePaged;

import java.util.List;

/**
 * Use case supporting table paged documentation
 */
@UseCase
public class TablePagedUC implements IDocumentationUseCase<TablePagedElement> {

    @Autowired
    private CountryService countryService;

    @Autowired
    private PersonService personService;

    private TablePagedElement model;

    private PageModel<CountryService.Country> originalPageModel;
    private PageModel<Person> pagedPeopleForRead;
    private PageModel<Person> pagedPeople;
    private PageModel<Person> pagedPeopleMergedColumns;
    private PageModel<Person> pagedPeopleColoredRows;

    @Override
    public void start(TablePagedElement model) {
        this.model = model;
        // independent page models for each table
        model.setPagedPeople(new PageModel<>(personService::findAllPeople));
        model.setPagedPeopleForRead(new PageModel<>(personService::findAllPeople));
        model.setPagedPeopleMergedColumns(new PageModel<>(personService::findAllPeople));
        model.setPagedPeopleColoredRows(new PageModel<>(personService::findAllPeople));
        model.setPageModelCountries(new PageModel<>(countryService::createPage));
        showForm(TablePagedForm.class, model);
    }

    /**
     * Removes country from pageModel
     *
     * @param country   - country to remove
     */
    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void removeCountry(CountryService.Country country) {
        countryService.remove(country);
        model.getPageModelCountries().refreshNeeded();
    }

    /**
     * Removes countries from pageModel
     *
     * @param countries - countries to remove
     */
    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void removeCountries(List<CountryService.Country> countries) {
        countryService.removeAll(countries);
        countries.clear(); // clear selection
        model.getPageModelCountries().refreshNeeded();
    }

    /**
     * Resets countriesTable
     */
    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void resetCountriesTable() {
        countryService.resetCountries();
        model.getPageModelCountries().refreshNeeded();
    }
}